package example.model.concert.actor

import akka.actor._
import com.typesafe.config.ConfigFactory
import example.ActorSpecBase

final class DefaultConcertActorWoPersistenceSpec
    extends ActorSpecBase(ActorSystem("default-concert-actor-wo-persistence", ConfigFactory.load("test-akka-cluster")))
    with ConcertActorBehaviors
    with ConcertActorClusterShardingBehaviors {

  private def createBehavior: ConcertActorBehaviorFactory = DefaultConcertActorWoPersistence

  classOf[DefaultConcertActorWoPersistence].getSimpleName should {
    behave like emptyConcertActor(new EmptyConcertActorFactory(createBehavior))
    behave like availableConcertActor(new AvailableConcertActorFactory(createBehavior))
    behave like cancelledConcertActor(new CancelledConcertActorFactory(createBehavior))
    behave like shardedActor(createBehavior)
  }

}
