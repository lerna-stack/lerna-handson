package example.model.concert.actor

import com.typesafe.config.ConfigFactory
import example.ActorSpecBase

final class DefaultConcertActorWithEventPersistenceSpec
    extends ActorSpecBase(ConfigFactory.load("test-akka-cluster"))
    with ConcertActorBehaviors
    with ConcertActorClusterShardingBehaviors {

  private def createBehavior: ConcertActorBehaviorFactory = DefaultConcertActorWithEventPersistence

  "DefaultConcertActorWithEventPersistence" should {
    behave like emptyConcertActor(new EmptyConcertActorFactory(createBehavior))
    behave like availableConcertActor(new AvailableConcertActorFactory(createBehavior))
    behave like cancelledConcertActor(new CancelledConcertActorFactory(createBehavior))
    behave like shardedActor(createBehavior)
  }

}
