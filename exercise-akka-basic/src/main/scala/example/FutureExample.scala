package example

import akka.actor.ActorSystem

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps

object FutureExample extends App {
  // ExecutionContext (Futureなどを処理する実行コンテキスト)
  // 今回は ActorSystem から持ってくる
  val system = ActorSystem("future-example")
  import system.dispatcher

  // Future[Int]
  // いつか結果が Int で戻る
  val future1: Future[Int] = Future {
    // すぐに実行されているわけではない
    // どこかのタイミングで実行される
    1 + 1
  }

  // 何か他の処理...

  // 結果を取り出してみる
  val result1: Int = Await.result(future1, 1 second)
  println(result1) // 2

  // 値がすぐにわかる場合はこのように書ける
  // こちらはすぐに実行されている
  val future2: Future[Int] = Future.successful(1)

  // とはいえ、外から見るといつ完了するかは分からないので、
  // 1秒くらい結果が来るか待ってみる
  val result2: Int = Await.result(future2, 1 second)
  println(result2) // 1

  // ActorSystemを終了する
  system.terminate()
}
