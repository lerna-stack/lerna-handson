package example.model.concert.actor

import example.ActorSpecBase
import example.model.ClusterShardingSpecLike
import example.model.concert.ConcertIdGeneratorSupport

trait ConcertActorClusterShardingBehaviors extends ConcertIdGeneratorSupport with ClusterShardingSpecLike {
  this: ActorSpecBase =>

  import example.model.concert.actor.ConcertActorProtocol._

  def shardedActor(createBehavior: ConcertActorBehaviorFactory): Unit = {

    val sharding = new ConcertActorClusterShardingFactory(DefaultConcertActor).create(system)

    "handle ConcertCommandRequests" in {
      val id        = newConcertId()
      val probe     = testKit.createTestProbe[ConcertCommandResponse]()
      val entityRef = sharding.entityRefFor(id)

      entityRef ! CreateConcertRequest(id, 2)(probe.ref)
      probe.expectMessageType[CreateConcertSucceeded]

      entityRef ! GetConcertRequest(id)(probe.ref)
      probe.expectMessageType[GetConcertSucceeded]

      entityRef ! BuyConcertTicketsRequest(id, 1)(probe.ref)
      probe.expectMessageType[BuyConcertTicketsSucceeded]

      entityRef ! CancelConcertRequest(id)(probe.ref)
      probe.expectMessageType[CancelConcertSucceeded]
    }

  }

}
