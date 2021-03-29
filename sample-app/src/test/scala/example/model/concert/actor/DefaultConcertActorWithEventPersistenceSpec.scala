package example.model.concert.actor

import akka.actor._
import example.ActorSpecBase

final class DefaultConcertActorWithEventPersistenceSpec
    extends ActorSpecBase(ActorSystem("default-concert-actor-with-event-persistence"))
    with ConcertActorBehaviors {
  private def props: Props = DefaultConcertActorWithEventPersistence.props

  classOf[DefaultConcertActorWithEventPersistence].getSimpleName should {
    behave like emptyConcertActor(new EmptyConcertActorFactory(props))
    behave like availableConcertActor(new AvailableConcertActorFactory(props))
    behave like cancelledConcertActor(new CancelledConcertActorFactory(props))
  }

}
