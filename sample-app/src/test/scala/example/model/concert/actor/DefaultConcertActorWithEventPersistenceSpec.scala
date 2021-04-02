package example.model.concert.actor

import akka.actor._
import example.ActorSpecBase

final class DefaultConcertActorWithEventPersistenceSpec
    extends ActorSpecBase(ActorSystem("default-concert-actor-with-event-persistence"))
    with ConcertActorBehaviors {

  private def createBehavior: ConcertActorBehaviorFactory = DefaultConcertActorWithEventPersistence

  classOf[DefaultConcertActorWithEventPersistence].getSimpleName should {
    behave like emptyConcertActor(new EmptyConcertActorFactory(createBehavior))
    behave like availableConcertActor(new AvailableConcertActorFactory(createBehavior))
    behave like cancelledConcertActor(new CancelledConcertActorFactory(createBehavior))
  }

}
