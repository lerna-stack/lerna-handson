package answer

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.persistence.testkit.scaladsl.EventSourcedBehaviorTestKit
import akka.persistence.typed.PersistenceId
import com.typesafe.config.{ Config, ConfigFactory }
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

object Answer1Spec {
  val config: Config =
    EventSourcedBehaviorTestKit.config
      .withFallback(ConfigFactory.load())
}

final class Answer1Spec
    extends ScalaTestWithActorTestKit(Answer1Spec.config)
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterEach {

  private val eventSourcedBehaviorTestKit =
    EventSourcedBehaviorTestKit[DefaultDoorActor.Command, DefaultDoorActor.Event, DefaultDoorActor.State](
      system,
      DefaultDoorActor(PersistenceId.ofUniqueId("default-door-actor-test")),
    )

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    eventSourcedBehaviorTestKit.clear()
  }

  "DoorActor" when {

    "ClosedState" should {

      "handle Open" in {
        val result = eventSourcedBehaviorTestKit.runCommand(DefaultDoorActor.Open)
        result.event shouldBe DefaultDoorActor.Opened
        result.state shouldBe DefaultDoorActor.OpenedState(1)
      }

      "handle Close" in {
        val result = eventSourcedBehaviorTestKit.runCommand(DefaultDoorActor.Close)
        result.hasNoEvents shouldBe true
        result.state shouldBe DefaultDoorActor.ClosedState(0)
      }

      "handle GetOpenedCount" in {
        val result = eventSourcedBehaviorTestKit.runCommand(DefaultDoorActor.GetOpenedCount)
        result.hasNoEvents shouldBe true
        result.reply shouldBe 0
        result.state shouldBe DefaultDoorActor.ClosedState(0)
      }

    }

    "OpenedState" should {

      "handle Open" in {
        // setup
        eventSourcedBehaviorTestKit.runCommand(DefaultDoorActor.Open)
        // test
        val result = eventSourcedBehaviorTestKit.runCommand(DefaultDoorActor.Open)
        result.hasNoEvents shouldBe true
        result.state shouldBe DefaultDoorActor.OpenedState(1)
      }

      "handle Close" in {
        // setup
        eventSourcedBehaviorTestKit.runCommand(DefaultDoorActor.Open)
        // test
        val result = eventSourcedBehaviorTestKit.runCommand(DefaultDoorActor.Close)
        result.event shouldBe DefaultDoorActor.Closed
        result.state shouldBe DefaultDoorActor.ClosedState(1)
      }

      "handle GetOpenedCount" in {
        // setup
        eventSourcedBehaviorTestKit.runCommand(DefaultDoorActor.Open)
        // test
        val result = eventSourcedBehaviorTestKit.runCommand(DefaultDoorActor.GetOpenedCount)
        result.hasNoEvents shouldBe true
        result.reply shouldBe 1
        result.state shouldBe DefaultDoorActor.OpenedState(1)
      }

    }

    "DoorActor" should {

      "increment it's openedCount" in {
        // For setup, Open and close the door several times
        eventSourcedBehaviorTestKit.runCommand(DefaultDoorActor.Open)
        eventSourcedBehaviorTestKit.runCommand(DefaultDoorActor.Close)
        eventSourcedBehaviorTestKit.runCommand(DefaultDoorActor.Open)
        eventSourcedBehaviorTestKit.runCommand(DefaultDoorActor.Close)

        // Verify the counter value is incremented.
        eventSourcedBehaviorTestKit.runCommand(DefaultDoorActor.Open)
        val resultInOpenedState = eventSourcedBehaviorTestKit.runCommand(DefaultDoorActor.GetOpenedCount)
        resultInOpenedState.reply shouldBe 3
        resultInOpenedState.state shouldBe DefaultDoorActor.OpenedState(3)

        eventSourcedBehaviorTestKit.runCommand(DefaultDoorActor.Close)
        val resultInClosedState = eventSourcedBehaviorTestKit.runCommand(DefaultDoorActor.GetOpenedCount)
        resultInClosedState.reply shouldBe 3
        resultInClosedState.state shouldBe DefaultDoorActor.ClosedState(3)
      }

    }

  }

}
