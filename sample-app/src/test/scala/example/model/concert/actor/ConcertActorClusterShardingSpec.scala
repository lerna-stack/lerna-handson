package example.model.concert.actor

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import example.ActorSpecBase
import example.model.ClusterShardingSpecLike
import example.model.concert.ConcertIdGenerator

final class ConcertActorClusterShardingSpec
    extends ActorSpecBase(ActorSystem("ConcertActorClusterShardingSpec", ConfigFactory.load("test-akka-cluster")))
    with ClusterShardingSpecLike {
  import example.model.concert.actor.ConcertActorProtocol._

  private val idGenerator = new ConcertIdGenerator()
  private val sharding    = new ConcertActorClusterShardingFactory(DefaultConcertActor).create(system)

  "handle ConcertCommandRequests" in {
    val id        = idGenerator.nextId()
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
