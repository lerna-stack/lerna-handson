package example

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.persistence.testkit.PersistenceTestKitPlugin
import akka.persistence.testkit.scaladsl.PersistenceTestKit
import akka.persistence.typed.PersistenceId
import com.typesafe.config.{ Config, ConfigFactory }
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

object CounterActorSpecWithPersistenceTestKit {
  val config: Config =
    PersistenceTestKitPlugin.config
      .withFallback(ConfigFactory.load())
}

final class CounterActorSpecWithPersistenceTestKit
    extends ScalaTestWithActorTestKit(CounterActorSpecWithPersistenceTestKit.config)
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterEach {

  private val persistenceTestKit = PersistenceTestKit(system)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    persistenceTestKit.clearAll()
  }

  "CounterActor" should {

    "handle an increment" in {

      val persistenceId = PersistenceId.ofUniqueId("counter-actor-test-1")
      val actor         = spawn(CounterActor(persistenceId))

      actor ! CounterActor.Increment
      persistenceTestKit.expectNextPersisted(persistenceId.id, CounterActor.Incremented)

    }

  }

}
