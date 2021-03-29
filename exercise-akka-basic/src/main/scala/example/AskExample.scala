package example

import akka.actor._
import akka.pattern.ask
import akka.util._

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps

object AskExample extends App {
  val system = ActorSystem("ask-example")

  final class EchoActor extends Actor {
    override def receive: Receive = {
      case msg => sender() ! msg
    }
  }

  // Askの結果をどの程度待つかを定義する
  // この値を過ぎると Future に失敗が返ってくる
  implicit val askTimeout: Timeout = 3 seconds

  // ?, ask でアクターへ問い合わせを行う
  // 返信があれば Future[Any] に結果が格納される
  val actorRef: ActorRef           = system.actorOf(Props(new EchoActor))
  val responseFuture1: Future[Any] = actorRef ? "test1"
  val responseFuture2: Future[Any] = actorRef.ask("test2")

  // 最大3秒間待って、結果を取り出して、コンソールに表示する。
  // 本番コードで Await.result はほぼ使わないので注意すること
  val result1: Any = Await.result(responseFuture1, 3 seconds)
  val result2: Any = Await.result(responseFuture2, 3 seconds)
  println(result1)
  println(result2)

  // Await で結果の処理を完了しているので、ActorSystemをすぐに終了させる
  system.terminate()
}
