package example.model.concert.actor

import example.ActorSpecBase

final class DefaultConcertActorWithEventPersistenceSpec
    extends ActorSpecBase()
    with ConcertActorBehaviors
    with ConcertActorClusterShardingBehaviors {

  private def createBehavior: ConcertActorBehaviorFactory = DefaultConcertActorWithEventPersistence

  "DefaultConcertActorWithEventPersistence" should {
    behave like emptyConcertActor(createBehavior)
    behave like availableConcertActor(createBehavior)
    behave like cancelledConcertActor(createBehavior)
    behave like shardedActor(createBehavior)
  }

}
