package example.model.concert.actor

final class DefaultConcertActorWithEventPersistenceSpec extends ConcertActorSpecBase {

  private def createBehavior: ConcertActorBehaviorFactory = DefaultConcertActorWithEventPersistence

  "DefaultConcertActorWithEventPersistence" should {
    behave like emptyConcertActor(createBehavior)
    behave like availableConcertActor(createBehavior)
    behave like cancelledConcertActor(createBehavior)
    behave like shardedActor(createBehavior)
  }

}
