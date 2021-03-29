package answer

import akka.actor._
import com.typesafe.config.{ Config, ConfigFactory }

// (A)
final class DefaultUpperCaseEchoActor extends Actor {
  override def receive: Receive = {
    // 文字列のメッセージのみを処理する
    case msg: String =>
      val upperCaseMsg = msg.toUpperCase
      println(upperCaseMsg)
      sender() ! upperCaseMsg
  }
}

object Answer1 extends App {
  val config: Config      = ConfigFactory.parseString("akka.log-dead-letters=0")
  val system: ActorSystem = ActorSystem("exercise1", config)

  // (B)
  val actorRef = system.actorOf(Props(new DefaultUpperCaseEchoActor()))
  actorRef ! "abc!" // ABC!
  actorRef ! 123    // 無視される
  actorRef ! "def?" // DEF?

  Thread.sleep(3000)
  system.terminate()
}
