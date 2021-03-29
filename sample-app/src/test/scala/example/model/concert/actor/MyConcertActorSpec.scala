package example.model.concert.actor

import akka.actor._
import example.ActorSpecBase
import example.testing.tags.ExerciseTest

@ExerciseTest
final class MyConcertActorSpec extends ActorSpecBase(ActorSystem("my-concert-actor-spec")) with ConcertActorBehaviors {
  private def props: Props = MyConcertActor.props

  classOf[MyConcertActor].getSimpleName should {
    behave like emptyConcertActor(new EmptyConcertActorFactory(props))
    behave like availableConcertActor(new AvailableConcertActorFactory(props))
    behave like cancelledConcertActor(new CancelledConcertActorFactory(props))
  }

}
