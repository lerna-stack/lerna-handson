package example.model.concert.actor

import akka.actor._
import example.ActorSpecBase

final class DefaultConcertActorSpec
    extends ActorSpecBase(ActorSystem("default-concert-actor"))
    with ConcertActorBehaviors {
  private def props: Props = DefaultConcertActor.props

  classOf[DefaultConcertActor].getSimpleName should {
    behave like emptyConcertActor(new EmptyConcertActorFactory(props))
    behave like availableConcertActor(new AvailableConcertActorFactory(props))
    behave like cancelledConcertActor(new CancelledConcertActorFactory(props))
  }

}
