package example.model.concert.actor

import akka.actor.ActorSystem
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.adapter._
import com.typesafe.config.ConfigFactory
import example.ActorSpecBase
import example.model.ClusterShardingSpecLike
import example.model.concert.ConcertIdGenerator
import example.model.concert.service.ConcertActorClusterShardingFactory

final class ConcertActorClusterShardingSpec
    extends ActorSpecBase(ActorSystem("ConcertActorClusterShardingSpec", ConfigFactory.load("test-akka-cluster")))
    with ClusterShardingSpecLike {
  import example.model.concert.actor.ConcertActorProtocol._

  private val idGenerator                                  = new ConcertIdGenerator()
  private val props                                        = DefaultConcertActor.props
  private val sharding                                     = new ConcertActorClusterShardingFactory(props).create(system.toClassic)
  private val shardRegion: ActorRef[ConcertCommandRequest] = sharding.shardRegion

  "handle ConcertCommandRequests" in {
    val id    = idGenerator.nextId()
    val probe = testKit.createTestProbe[ConcertCommandResponse]()

    shardRegion ! CreateConcertRequest(id, 2)(probe.ref)
    probe.expectMessageType[CreateConcertSucceeded]

    shardRegion ! GetConcertRequest(id)(probe.ref)
    probe.expectMessageType[GetConcertSucceeded]

    shardRegion ! BuyConcertTicketsRequest(id, 1)(probe.ref)
    probe.expectMessageType[BuyConcertTicketsSucceeded]

    shardRegion ! CancelConcertRequest(id)(probe.ref)
    probe.expectMessageType[CancelConcertSucceeded]
  }

}
