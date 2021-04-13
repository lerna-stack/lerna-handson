package example.model.concert.actor

final class DefaultConcertActorSpec extends ConcertActorSpecBase {

  private def createBehavior: ConcertActorBehaviorFactory = DefaultConcertActor

  "DefaultConcertActor" should {
    behave like emptyConcertActor(createBehavior)
    behave like availableConcertActor(createBehavior)
    behave like cancelledConcertActor(createBehavior)
    behave like shardedActor(createBehavior)
  }

}
