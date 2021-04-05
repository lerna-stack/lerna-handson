package example.model.concert.service

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.adapter._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{ CurrentClusterState, MemberUp }
import com.typesafe.config.ConfigFactory
import example.ActorSpecBase

abstract class BoxOfficeServiceSpecBase(actorSystemName: String)
    extends ActorSpecBase(
      ActorSystem(
        actorSystemName,
        ConfigFactory.load("test-akka-cluster"),
      ),
    ) {

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    // TODO Use Typed Cluster
    val probe   = testKit.createTestProbe[Any]()
    val cluster = Cluster(system)
    cluster.subscribe(probe.ref.toClassic, classOf[MemberUp])
    probe.expectMessageType[CurrentClusterState]

    cluster.join(cluster.selfAddress)
    probe
      .receiveMessages(1)
      .collect({
        case MemberUp(member) if member.address == cluster.selfAddress => member
      }).size shouldBe 1

    println(s"[DONE] ${cluster.selfMember} join to cluster.")
    cluster.unsubscribe(probe.ref.toClassic)

    // Cluster Sharding のセットアップが終わるまでしばらく待つ
    Thread.sleep(5000)
  }

}
