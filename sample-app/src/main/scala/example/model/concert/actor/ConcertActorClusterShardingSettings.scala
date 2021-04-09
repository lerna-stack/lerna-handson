package example.model.concert.actor

import akka.actor.typed.ActorSystem

object ConcertActorClusterShardingSettings {
  def apply(system: ActorSystem[Nothing]): ConcertActorClusterShardingSettings = {
    val config = system.settings.config.getConfig("example.concert-actor-cluster-sharding")
    new ConcertActorClusterShardingSettings(
      config.getString("entity-type-key-name"),
    )
  }
}

/** ConcertActorClusterSharding の設定
  */
final class ConcertActorClusterShardingSettings(
    val entityTypeKeyName: String,
)
