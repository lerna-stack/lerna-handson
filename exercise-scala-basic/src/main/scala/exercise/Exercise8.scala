package exercise

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{ Failure, Success }

object Exercise8 extends App {

  def doubling(source: Future[Int]): Future[Int] = {
    // (A) Future[Int] を受け取り、要素を2倍して返すメソッドを実装してみよう
    ???
  }

  def mapToDouble(source: Future[Int]): Future[Double] = {
    // (B) Future[Int] を受け取り、浮動小数点数(Double)に変換して返すメソッドを実装してみよう
    // 123.toDouble で浮動小数に変換できる
    ???
  }

  def parseInt(source: Future[String]): Future[Int] = {
    // (C) Future[String] を受け取り、整数にパースするメソッドを実装してみよう
    // "123".toInt で整数に変換できる
    ???
  }

  // ここより下は簡単なテストコード
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

}
