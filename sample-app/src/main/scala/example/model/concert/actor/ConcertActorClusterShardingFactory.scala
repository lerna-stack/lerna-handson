package example.model.concert.actor

import akka.actor.typed.ActorSystem
import example.model.concert.actor.ConcertActorBase.ConcertActorClusterSharding

final class ConcertActorClusterShardingFactory(
    behaviorFactory: ConcertActorBehaviorFactory,
) {

  /** ShardedConcertActor の ClusterSharding を開始する。
    */
  def create(system: ActorSystem[Nothing]): ConcertActorBase.ConcertActorClusterSharding = {
    val concertActorConfig = ConcertActorConfig(system)
    new ConcertActorClusterSharding(system, concertActorConfig, behaviorFactory)
  }
}
