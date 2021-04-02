package example.model.concert.actor

import akka.actor._
import example.ActorSpecBase

final class DefaultConcertActorSpec
    extends ActorSpecBase(ActorSystem("default-concert-actor"))
    with ConcertActorBehaviors {

  private def createBehavior: ConcertActorBehaviorFactory = DefaultConcertActor

  classOf[DefaultConcertActor].getSimpleName should {
    behave like emptyConcertActor(new EmptyConcertActorFactory(createBehavior))
    behave like availableConcertActor(new AvailableConcertActorFactory(createBehavior))
    behave like cancelledConcertActor(new CancelledConcertActorFactory(createBehavior))
  }

}
