package example.model.concert.actor

import akka.actor.typed.ActorSystem

final class ConcertActorClusterShardingFactory(
    behaviorFactory: ConcertActorBehaviorFactory,
) {
  def create(system: ActorSystem[Nothing]): ConcertActorBase.ConcertActorClusterSharding = {
    val concertActorConfig = ConcertActorConfig(system)
    ConcertActorBase.startClusterSharding(system, concertActorConfig, behaviorFactory)
  }
}
