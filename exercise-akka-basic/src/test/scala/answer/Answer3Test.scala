package answer

import akka.actor._
import akka.testkit.{ ImplicitSender, TestKit }
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

final class Answer3Test
    extends TestKit(ActorSystem("answer3"))
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll
    with ImplicitSender {
  override def afterAll(): Unit = {
    shutdown(system)
  }

  "DefaultCounterActor should handle int message" in {
    val actorRef = system.actorOf(Props(new DefaultCounterActor()))

    actorRef ! 3
    expectMsg(3)

    actorRef ! -1
    expectMsg(2)

    actorRef ! 126
    expectMsg(128)
  }

  "DefaultCounterActor should handle non-int message" in {
    val actorRef = system.actorOf(Props(new DefaultCounterActor()))

    actorRef ! 3
    expectMsg(3)

    actorRef ! -2
    expectMsg(1)

    actorRef ! "reset"
    expectMsg(0)

    actorRef ! 4
    expectMsg(4)

    actorRef ! -1
    expectMsg(3)
  }

}
