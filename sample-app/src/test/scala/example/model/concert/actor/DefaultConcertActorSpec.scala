package example.model.concert.actor

import example.ActorSpecBase

final class DefaultConcertActorSpec
    extends ActorSpecBase()
    with ConcertActorBehaviors
    with ConcertActorClusterShardingBehaviors {

  private def createBehavior: ConcertActorBehaviorFactory = DefaultConcertActor

  "DefaultConcertActor" should {
    behave like emptyConcertActor(new EmptyConcertActorFactory(createBehavior))
    behave like availableConcertActor(new AvailableConcertActorFactory(createBehavior))
    behave like cancelledConcertActor(new CancelledConcertActorFactory(createBehavior))
    behave like shardedActor(createBehavior)
  }

}
