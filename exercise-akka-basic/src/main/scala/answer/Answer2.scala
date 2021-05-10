package answer

import akka.actor.typed.scaladsl.AskPattern.{ schedulerFromActorSystem, Askable }
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.util.Timeout
import com.typesafe.config.{ Config, ConfigFactory }

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.{ Failure, Success }

object DefaultUpperCaseEchoActor {
  final case class Message(value: String, replyTo: ActorRef[String])
  def apply(): Behavior[Message] = {
    // (A) アクターの定義はここに書こう
    Behaviors.receiveMessage { message: Message =>
      message.replyTo ! message.value.toUpperCase
      Behaviors.same
    }
  }
}

object Answer2 extends App {
  val config: Config =
    ConfigFactory.parseString("akka.log-dead-letters=0")
  implicit val askTimeout: Timeout = 3.seconds
  implicit val system: ActorSystem[DefaultUpperCaseEchoActor.Message] =
    ActorSystem(DefaultUpperCaseEchoActor(), "answer2", config)
  import system.executionContext

  val actorRef: ActorRef[DefaultUpperCaseEchoActor.Message] = system
  // (B) メッセージを送って、送り返されてきたメッセージをコンソールに表示してみよう
  val response: Future[String] =
    actorRef.ask(replyTo => DefaultUpperCaseEchoActor.Message("hello", replyTo))
  response.onComplete {
    case Success(value) =>
      println(value)
    case Failure(exception) =>
      println(exception)
  }

  // アクターがメッセージを処理完了するまで適当に待って終了する
  Thread.sleep(3000)
  system.terminate()
}
