package example.application.command

import akka.cluster.ClusterEvent.MemberUp
import akka.cluster.typed.{ Cluster, Join, Subscribe, Unsubscribe }
import example.ActorSpecBase
import org.scalatest.BeforeAndAfterAll

trait ClusterShardingSpecLike extends BeforeAndAfterAll { this: ActorSpecBase =>

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    waitToInitializeClusterSharding()
  }

  def waitToInitializeClusterSharding(): Unit = {
    // TODO There may be better way to complete this purpose.
    val probe   = testKit.createTestProbe[MemberUp]()
    val cluster = Cluster(system)
    cluster.subscriptions ! Subscribe(probe.ref, classOf[MemberUp])

    cluster.manager ! Join(cluster.selfMember.address)
    probe
      .receiveMessages(1)
      .collect({
        case MemberUp(member) if member.address == cluster.selfMember.address => member
      }).size shouldBe 1

    println(s"[DONE] ${cluster.selfMember} join to cluster.")
    cluster.subscriptions ! Unsubscribe[MemberUp](probe.ref)

    // Cluster Sharding のセットアップが終わるまでしばらく待つ
    Thread.sleep(5000)
  }

}
