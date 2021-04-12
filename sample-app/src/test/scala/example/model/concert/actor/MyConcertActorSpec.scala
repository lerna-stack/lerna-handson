package example.model.concert.actor

import example.ActorSpecBase
import example.testing.tags.ExerciseTest

@ExerciseTest
final class MyConcertActorSpec
    extends ActorSpecBase()
    with ConcertActorBehaviors
    with ConcertActorClusterShardingBehaviors {

  private def createBehavior: ConcertActorBehaviorFactory = MyConcertActor

  "MyConcertActor" should {
    behave like emptyConcertActor(new EmptyConcertActorFactory(createBehavior))
    behave like availableConcertActor(new AvailableConcertActorFactory(createBehavior))
    behave like cancelledConcertActor(new CancelledConcertActorFactory(createBehavior))
    behave like shardedActor(createBehavior)
  }

}
