package answer

import akka.actor.testkit.typed.scaladsl.{ ScalaTestWithActorTestKit, TestProbe }
import akka.actor.typed.ActorRef
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

final class Answer3 extends ScalaTestWithActorTestKit() with AnyWordSpecLike with Matchers {

  "DefaultUpperCaseEchoActor should echo back upper-case-message" in {
    val actorRef: ActorRef[DefaultUpperCaseEchoActor.Message] =
      spawn(DefaultUpperCaseEchoActor())
    val probe: TestProbe[String] =
      createTestProbe[String]()

    actorRef ! DefaultUpperCaseEchoActor.Message("abc", probe.ref)
    probe.expectMessage("ABC")

    actorRef ! DefaultUpperCaseEchoActor.Message("123def!?", probe.ref)
    probe.expectMessage("123DEF!?")
  }

}
