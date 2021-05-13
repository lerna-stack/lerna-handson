package example

import akka.actor.typed.scaladsl.AskPattern.{ schedulerFromActorSystem, Askable }
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.util.Timeout

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration.DurationInt

// 受け取ったメッセージをエコーするアクター
// アクターにてメッセージへの返信を実現する方法を例示することが目的である
object EchoActor {

  // EchoActor が処理できるメッセージ を定義する
  // 返信先アクター `replyTo` への参照をメッセージに含めておき、
  // メッセージを送信する側が `replyTo` を指定することでメッセージの返信を実現できる
  case class Message(value: String, replyTo: ActorRef[String])

  def apply(): Behavior[Message] = {
    Behaviors.receiveMessage { message =>
      // 返信先アクター `replyTo` に返信メッセージを送信することで返信が実現できる
      message.replyTo ! message.value
      Behaviors.same
    }
  }

}

object EchoActorExample extends App {
  implicit val system: ActorSystem[EchoActor.Message] =
    ActorSystem(EchoActor(), "my-echo-actor-system")
  import system.executionContext
  implicit val askTimeout: Timeout = 3.seconds

  // EchoActor
  val actorRef: ActorRef[EchoActor.Message] = system

  // 返信メッセージを Future[T] として受け取れる
  val response: Future[String] =
    system.ask(replyTo => EchoActor.Message("Hello World!", replyTo))

  response.foreach(repliedMessage =>
    // "Hello World!" が表示される
    println(repliedMessage),
  )

  // Futureが完了するまで待ってから、 ActorSystem を終了させる
  Await.ready(response, 3.seconds)
  system.terminate()
}
