package example.model.concert.actor

import akka.actor.typed.ActorSystem

object ConcertActorClusterShardingSettings {
  def apply(system: ActorSystem[Nothing]): ConcertActorClusterShardingSettings = {
    val config = system.settings.config.getConfig("example.concert-actor-cluster-sharding")
    new ConcertActorClusterShardingSettings(
      config.getString("shard-name"),
    )
  }
}

/** ConcertActorClusterSharding の設定
  */
final class ConcertActorClusterShardingSettings(
    /** シャード名
      */
    val shardName: String,
)
