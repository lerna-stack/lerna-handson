package exercise

import akka.actor.typed.scaladsl.AskPattern.{ schedulerFromActorSystem, Askable }
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.util.Timeout
import com.typesafe.config.{ Config, ConfigFactory }

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.{ Failure, Success }

/** 演習2: [[UpperCaseEchoActor]] を実装しよう。
  *
  * (A) 次のようなアクターを実装しよう。
  *  - メッセージ(文字列)を大文字に変換し、`replyTo` に送り返す。
  *  - 文字列を大文字に変換するには、`String.toUpperCase()` が使える。
  *
  * (B) メッセージを送って、送り返されてきたメッセージをコンソールに表示してみよう。
  *
  * 解答は [[answer.DefaultUpperCaseEchoActor]] で確認できる。
  */
object UpperCaseEchoActor {

  /** [[UpperCaseEchoActor]] が扱うメッセージ */
  final case class Message(value: String, replyTo: ActorRef[String])

  def apply(): Behavior[Message] = {
    // (A) アクターの定義はここに書こう
    ???
  }
}

object Exercise2 extends App {
  val config: Config =
    ConfigFactory.parseString("akka.log-dead-letters=0")
  implicit val askTimeout: Timeout = 3.seconds
  implicit val system: ActorSystem[UpperCaseEchoActor.Message] =
    ActorSystem(UpperCaseEchoActor(), "exercise2", config)
  import system.executionContext

  val actorRef: ActorRef[UpperCaseEchoActor.Message] = system
  // (B) メッセージを送って、送り返されてきたメッセージをコンソールに表示してみよう
  ???

  // アクターがメッセージを処理完了するまで適当に待って終了する
  Thread.sleep(3000)
  system.terminate()
}
