package example.model.concert.actor

import akka.actor._
import example.ActorSpecBase

final class DefaultConcertActorWoPersistenceSpec
    extends ActorSpecBase(ActorSystem("default-concert-actor-wo-persistence"))
    with ConcertActorBehaviors {
  private def props: Props = DefaultConcertActorWoPersistence.props

  classOf[DefaultConcertActorWoPersistence].getSimpleName should {
    behave like emptyConcertActor(new EmptyConcertActorFactory(props))
    behave like availableConcertActor(new AvailableConcertActorFactory(props))
    behave like cancelledConcertActor(new CancelledConcertActorFactory(props))
  }

}
