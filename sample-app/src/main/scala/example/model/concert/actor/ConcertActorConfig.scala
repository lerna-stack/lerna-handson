package example.model.concert.actor

import akka.actor.typed.ActorSystem

object ConcertActorConfig {
  def apply(system: ActorSystem[Nothing]): ConcertActorConfig = {
    val config = system.settings.config.getConfig("example.concert-actor")
    new ConcertActorConfig(
      config.getString("shard-name"),
    )
  }
}

/** ShardedConcertActor の設定
  */
final class ConcertActorConfig(
    /** シャード名
      */
    val shardName: String,
)
