package example

import scala.annotation.nowarn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{ Failure, Success }

// コピーしてスライドに載せる都合でクラスに付与している
// ゼロ除算で例外を意図的に起こしているため
@nowarn("cat=lint-constant")
object FutureFailureExample extends App {
  // 失敗する
  val failureFuture: Future[Int] = Future {
    // ゼロ除算エラーにしてみる
    1 / 0
  }

  // 実行が完了したときの処理
  failureFuture.onComplete {
    case Success(value) =>
      println(value) // 到達しない
    case Failure(exception) =>
      println(exception) // java.lang.ArithmeticException が表示される
  }
  // failureFuture の実行完了まで最大1秒まつ
  Await.ready(failureFuture, 1 second)
}
