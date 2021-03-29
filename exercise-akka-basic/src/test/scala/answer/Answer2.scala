package answer

import akka.actor._
import akka.testkit.{ ImplicitSender, TestKit }
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

final class Answer2
    extends TestKit(ActorSystem("answer2", ConfigFactory.parseString("akka.log-dead-letters=0")))
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll
    with ImplicitSender {
  override def afterAll(): Unit = {
    shutdown(system)
  }

  "DefaultUpperCaseEchoActor should echo back upper-case-message" in {
    val actorRef = system.actorOf(Props(new DefaultUpperCaseEchoActor()))

    actorRef ! "abc"
    expectMsg("ABC")

    actorRef ! "123def!?"
    expectMsg("123DEF!?")
  }

  "DefaultUpperCaseEchoActor should not echo back for non-string-value" in {
    val actorRef = system.actorOf(Props(new DefaultUpperCaseEchoActor()))

    val intValue: Int = 128
    actorRef ! intValue
    expectNoMessage()

    val doubleValue: Double = 8.7
    actorRef ! doubleValue
    expectNoMessage()
  }

}
