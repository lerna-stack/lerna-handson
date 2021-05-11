package exercise

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.persistence.testkit.scaladsl.EventSourcedBehaviorTestKit
import akka.persistence.typed.PersistenceId
import com.typesafe.config.{ Config, ConfigFactory }
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import testing.tags.ExerciseTest

object Exercise1Spec {
  val config: Config =
    EventSourcedBehaviorTestKit.config
      .withFallback(ConfigFactory.load())
}

@ExerciseTest
final class Exercise1Spec
    extends ScalaTestWithActorTestKit(Exercise1Spec.config)
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterEach {

  private val eventSourcedBehaviorTestKit =
    EventSourcedBehaviorTestKit[MyDoorActor.Command, MyDoorActor.Event, MyDoorActor.State](
      system,
      MyDoorActor(PersistenceId.ofUniqueId("my-door-actor-test")),
    )

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    eventSourcedBehaviorTestKit.clear()
  }

  "DoorActor" when {

    "ClosedState" should {

      "handle Open" in {
        val result = eventSourcedBehaviorTestKit.runCommand(MyDoorActor.Open)
        result.event shouldBe MyDoorActor.Opened
        result.state shouldBe MyDoorActor.OpenedState(1)
      }

      "handle Close" in {
        val result = eventSourcedBehaviorTestKit.runCommand(MyDoorActor.Close)
        result.hasNoEvents shouldBe true
        result.state shouldBe MyDoorActor.ClosedState(0)
      }

      "handle GetOpenedCount" in {
        val result = eventSourcedBehaviorTestKit.runCommand(MyDoorActor.GetOpenedCount)
        result.hasNoEvents shouldBe true
        result.reply shouldBe 0
        result.state shouldBe MyDoorActor.ClosedState(0)
      }

    }

    "OpenedState" should {

      "handle Open" in {
        // setup
        eventSourcedBehaviorTestKit.runCommand(MyDoorActor.Open)
        // test
        val result = eventSourcedBehaviorTestKit.runCommand(MyDoorActor.Open)
        result.hasNoEvents shouldBe true
        result.state shouldBe MyDoorActor.OpenedState(1)
      }

      "handle Close" in {
        // setup
        eventSourcedBehaviorTestKit.runCommand(MyDoorActor.Open)
        // test
        val result = eventSourcedBehaviorTestKit.runCommand(MyDoorActor.Close)
        result.event shouldBe MyDoorActor.Closed
        result.state shouldBe MyDoorActor.ClosedState(1)
      }

      "handle GetOpenedCount" in {
        // setup
        eventSourcedBehaviorTestKit.runCommand(MyDoorActor.Open)
        // test
        val result = eventSourcedBehaviorTestKit.runCommand(MyDoorActor.GetOpenedCount)
        result.hasNoEvents shouldBe true
        result.reply shouldBe 1
        result.state shouldBe MyDoorActor.OpenedState(1)
      }

    }

    "DoorActor" should {

      "increment it's openedCount" in {
        // For setup, Open and close the door several times
        eventSourcedBehaviorTestKit.runCommand(MyDoorActor.Open)
        eventSourcedBehaviorTestKit.runCommand(MyDoorActor.Close)
        eventSourcedBehaviorTestKit.runCommand(MyDoorActor.Open)
        eventSourcedBehaviorTestKit.runCommand(MyDoorActor.Close)

        // Verify the counter value is incremented.
        eventSourcedBehaviorTestKit.runCommand(MyDoorActor.Open)
        val resultInOpenedState = eventSourcedBehaviorTestKit.runCommand(MyDoorActor.GetOpenedCount)
        resultInOpenedState.reply shouldBe 3
        resultInOpenedState.state shouldBe MyDoorActor.OpenedState(3)

        eventSourcedBehaviorTestKit.runCommand(MyDoorActor.Close)
        val resultInClosedState = eventSourcedBehaviorTestKit.runCommand(MyDoorActor.GetOpenedCount)
        resultInClosedState.reply shouldBe 3
        resultInClosedState.state shouldBe MyDoorActor.ClosedState(3)
      }

    }

  }

}
