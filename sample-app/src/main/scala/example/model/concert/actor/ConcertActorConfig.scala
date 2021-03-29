package example.model.concert.actor

import akka.actor.ActorSystem

object ConcertActorConfig {
  def apply(system: ActorSystem): ConcertActorConfig = {
    val config = system.settings.config.getConfig("example.concert-actor")
    new ConcertActorConfig(
      config.getString("shard-name"),
      config.getInt("shard-count"),
    )
  }
}

/** ShardedConcertActor の設定
  */
final class ConcertActorConfig(
    /** シャード名
      */
    val shardName: String,
    /** シャード数
      */
    val shardCount: Int,
)
