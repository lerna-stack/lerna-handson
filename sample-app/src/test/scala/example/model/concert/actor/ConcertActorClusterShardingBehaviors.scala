package example.model.concert.actor

import example.ActorSpecBase
import example.model.ClusterShardingSpecLike
import example.model.concert.ConcertIdGeneratorSupport

trait ConcertActorClusterShardingBehaviors extends ConcertIdGeneratorSupport with ClusterShardingSpecLike {
  this: ActorSpecBase =>

  import example.model.concert.actor.ConcertActor._

  def shardedActor(createBehavior: ConcertActorBehaviorFactory): Unit = {

    val sharding = new ConcertActorClusterSharding(system, createBehavior)

    "handle ConcertCommandRequests" in {
      val id        = newConcertId()
      val probe     = testKit.createTestProbe[ConcertCommandResponse]()
      val entityRef = sharding.entityRefFor(id)

      entityRef ! Create(2, probe.ref)
      probe.expectMessageType[CreateConcertSucceeded]

      entityRef ! Get(probe.ref)
      probe.expectMessageType[GetConcertSucceeded]

      entityRef ! BuyTickets(1, probe.ref)
      probe.expectMessageType[BuyConcertTicketsSucceeded]

      entityRef ! Cancel(probe.ref)
      probe.expectMessageType[CancelConcertSucceeded]
    }

  }

}
