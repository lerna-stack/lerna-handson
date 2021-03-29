package answer

import akka.actor.ActorSystem

import scala.concurrent.duration._
import scala.concurrent._
import scala.language.postfixOps
import scala.util.{ Failure, Success }

object Answer4 extends App {
  // ExecutionContext (Futureなどを処理する実行コンテキスト)
  // 今回は ActorSystem から持ってくる
  val system = ActorSystem("answer4")
  import system.dispatcher

  def doubling(source: Future[Int]): Future[Int] = {
    source.map(_ * 2)
  }

  def mapToDouble(source: Future[Int]): Future[Double] = {
    source.map(_.toDouble)
  }

  def parseInt(source: Future[String]): Future[Int] = {
    source.map(_.toInt)
  }

  try {
    val future200 = doubling(Future.successful(100))
    assert(Await.result(future200, 1 second) == 200)

    val future123 = mapToDouble(Future.successful(123))
    assert((Await.result(future123, 1 second) - 123).abs < 1e-5)

    val parseSuccess = parseInt(Future.successful("1024"))
    assert(Await.result(parseSuccess, 1 second) == 1024)

    val parseFailure = parseInt(Future.successful("abcdefg"))
    parseFailure.onComplete {
      case Success(value)     => assert(false)
      case Failure(exception) => assert(true)
    }
    Await.ready(parseFailure, 1 second)

    println("OK")
  } finally {
    // ActorSystem を終了させる
    system.terminate()
  }
}
