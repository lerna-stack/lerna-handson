package example.model.concert.actor

import akka.actor.typed.ActorSystem

final class ConcertActorClusterShardingFactory(
    behaviorFactory: ConcertActorBehaviorFactory,
) {

  /** ShardedConcertActor の ClusterSharding を開始する。
    */
  def create(system: ActorSystem[Nothing]): ConcertActorClusterSharding = {
    val concertActorConfig = ConcertActorConfig(system)
    new ConcertActorClusterSharding(system, concertActorConfig, behaviorFactory)
  }
}
