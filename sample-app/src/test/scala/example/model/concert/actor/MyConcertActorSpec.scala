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
    behave like emptyConcertActor(createBehavior)
    behave like availableConcertActor(createBehavior)
    behave like cancelledConcertActor(createBehavior)
    behave like shardedActor(createBehavior)
  }

}
