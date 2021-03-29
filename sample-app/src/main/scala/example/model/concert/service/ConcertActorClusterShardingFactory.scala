package example.model.concert.service

import akka.actor.{ ActorSystem, Props }
import example.model.concert.actor._

object ConcertActorClusterShardingFactory {
  type ConcertActorProps = Props
}
final class ConcertActorClusterShardingFactory(
    props: ConcertActorClusterShardingFactory.ConcertActorProps,
) {
  def create(system: ActorSystem): ConcertActorBase.ConcertActorClusterSharding = {
    // 起動時に ShardedConcertActor の ClusterSharding を開始する。
    val concertActorConfig = ConcertActorConfig(system)
    ConcertActorBase.startClusterSharding(system, concertActorConfig, props)
  }
}
