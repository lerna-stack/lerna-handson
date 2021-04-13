package example.model.concert.actor

import example.testing.tags.ExerciseTest

@ExerciseTest
final class MyConcertActorSpec extends ConcertActorSpecBase {

  private def createBehavior: ConcertActorBehaviorFactory = MyConcertActor

  "MyConcertActor" should {
    behave like emptyConcertActor(createBehavior)
    behave like availableConcertActor(createBehavior)
    behave like cancelledConcertActor(createBehavior)
    behave like shardedActor(createBehavior)
    // スナップショットの実装時にコメントアウトを外す (この行はそのまま)
    // behave like snapshotPersistenceActor(createBehavior)
  }

}
