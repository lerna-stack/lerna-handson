package example.model

import akka.actor.typed.scaladsl.adapter._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{ CurrentClusterState, MemberUp }
import example.ActorSpecBase
import org.scalatest.BeforeAndAfterAll

trait ClusterShardingSpecLike extends BeforeAndAfterAll { this: ActorSpecBase =>

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    waitToInitializeClusterSharding()
  }

  def waitToInitializeClusterSharding(): Unit = {
    // TODO Use Typed Cluster
    // TODO There may be better way to complete this purpose.
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
