package example.model.concert.actor

import akka.persistence.testkit.PersistenceTestKitPlugin
import com.typesafe.config.{ Config, ConfigFactory }
import example.ActorSpecBase

object ConcertActorSpecBase {
  private val config: Config = {
    PersistenceTestKitPlugin.config
      .withFallback(ConfigFactory.load)
  }
}

abstract class ConcertActorSpecBase
    extends ActorSpecBase(ConcertActorSpecBase.config)
    with ConcertActorBehaviors
    with ConcertActorClusterShardingBehaviors
