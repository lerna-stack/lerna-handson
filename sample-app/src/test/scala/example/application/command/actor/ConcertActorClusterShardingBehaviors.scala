package example.application.command.actor

import example.ActorSpecBase
import example.adapter.ConcertIdGeneratorSupport
import example.application.command.ClusterShardingSpecLike

trait ConcertActorClusterShardingBehaviors extends ConcertIdGeneratorSupport with ClusterShardingSpecLike {
  this: ActorSpecBase =>

  import example.application.command.actor.ConcertActor._

  def shardedActor(createBehavior: ConcertActorBehaviorFactory): Unit = {

    val sharding = new ConcertActorClusterSharding(system, createBehavior)

    "handle ConcertActor's commands" in {
      val id        = newConcertId()
      val probe     = testKit.createTestProbe[Response]()
      val entityRef = sharding.entityRefFor(id)

      entityRef ! Create(2, probe.ref)
      probe.expectMessageType[CreateSucceeded]

      entityRef ! Get(probe.ref)
      probe.expectMessageType[GetSucceeded]

      entityRef ! BuyTickets(1, probe.ref)
      probe.expectMessageType[BuyTicketsSucceeded]

      entityRef ! Cancel(probe.ref)
      probe.expectMessageType[CancelSucceeded]
    }

  }

}
