package example.model.concert.actor

import example.ActorSpecBase
import example.model.ClusterShardingSpecLike
import example.model.concert.ConcertIdGeneratorSupport

trait ConcertActorClusterShardingBehaviors extends ConcertIdGeneratorSupport with ClusterShardingSpecLike {
  this: ActorSpecBase =>

  import example.model.concert.actor.ConcertActorProtocol._

  def shardedActor(createBehavior: ConcertActorBehaviorFactory): Unit = {

    val sharding = ConcertActorClusterSharding.init(system, createBehavior)

    "handle ConcertCommandRequests" in {
      val id        = newConcertId()
      val probe     = testKit.createTestProbe[ConcertCommandResponse]()
      val entityRef = sharding.entityRefFor(id)

      entityRef ! CreateConcertRequest(2, probe.ref)
      probe.expectMessageType[CreateConcertSucceeded]

      entityRef ! GetConcertRequest(probe.ref)
      probe.expectMessageType[GetConcertSucceeded]

      entityRef ! BuyConcertTicketsRequest(1, probe.ref)
      probe.expectMessageType[BuyConcertTicketsSucceeded]

      entityRef ! CancelConcertRequest(probe.ref)
      probe.expectMessageType[CancelConcertSucceeded]
    }

  }

}
