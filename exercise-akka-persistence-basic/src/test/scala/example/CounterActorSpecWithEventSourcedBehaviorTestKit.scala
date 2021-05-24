package example

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.persistence.testkit.scaladsl.EventSourcedBehaviorTestKit
import akka.persistence.typed.PersistenceId
import com.typesafe.config.{ Config, ConfigFactory }
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

object CounterActorSpecWithEventSourcedBehaviorTestKit {
  val config: Config =
    EventSourcedBehaviorTestKit.config
      .withFallback(ConfigFactory.load())
}

final class CounterActorSpecWithEventSourcedBehaviorTestKit
    extends ScalaTestWithActorTestKit(CounterActorSpecWithEventSourcedBehaviorTestKit.config)
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterEach {

  private val eventSourcedBehaviorTestKit =
    EventSourcedBehaviorTestKit[CounterActor.Command, CounterActor.Event, CounterActor.State](
      system,
      CounterActor(PersistenceId.ofUniqueId("counter-actor-test")),
    )

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    eventSourcedBehaviorTestKit.clear()
  }

  "CounterActor" should {

    "handle an increment" in {

      val result = eventSourcedBehaviorTestKit.runCommand(CounterActor.Increment)
      result.event shouldBe CounterActor.Incremented
      result.state shouldBe CounterActor.State(1)

    }

  }

}
