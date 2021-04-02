package example.model.concert.actor

import akka.actor._
import example.ActorSpecBase
import example.testing.tags.ExerciseTest

@ExerciseTest
final class MyConcertActorSpec extends ActorSpecBase(ActorSystem("my-concert-actor-spec")) with ConcertActorBehaviors {

  private def createBehavior: ConcertActorBehaviorFactory = MyConcertActor

  classOf[MyConcertActor].getSimpleName should {
    behave like emptyConcertActor(new EmptyConcertActorFactory(createBehavior))
    behave like availableConcertActor(new AvailableConcertActorFactory(createBehavior))
    behave like cancelledConcertActor(new CancelledConcertActorFactory(createBehavior))
  }

}
