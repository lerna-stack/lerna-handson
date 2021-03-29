package example

import akka.actor.ActorSystem

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps

object FutureMapExample extends App {
  // ExecutionContext (Futureなどを処理する実行コンテキスト)
  // 今回は ActorSystem から持ってくる
  val system = ActorSystem("future-example")
  import system.dispatcher

  // map を使って 自由に変換できる
  // map の中身は非同期でいつか実行される
  val intFuture: Future[Int] = Future.successful(1)
  val stringFuture: Future[String] =
    intFuture
      .map(_ * 2)      // 2 倍にして
      .map(_.toString) // 文字列にする
  val stringValue = Await.result(stringFuture, 1 second)
  println(stringValue) // "2"

  // 失敗したものに対する map は実行されない
  val failureFuture: Future[Int] =
    Future.failed[Int](new RuntimeException())
  val doubledFuture: Future[Int] =
    failureFuture.map(value => {
      // この中身は実行されない
      println(value)
      value * 2
    })
  Await.ready(failureFuture, 1 second)

  // ActorSystemを終了する
  system.terminate()
}
