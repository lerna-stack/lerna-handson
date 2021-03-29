package example.model.concert.service

import akka.actor.ActorSystem
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

  override protected def afterAll(): Unit = {
    super.afterAll()
    shutdown(system)
  }

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    val cluster = Cluster(system)
    cluster.subscribe(self, classOf[MemberUp])
    expectMsgType[CurrentClusterState]

    cluster.join(cluster.selfAddress)
    receiveN(1)
      .collect({
        case MemberUp(member) if member.address == cluster.selfAddress => member
      }).size shouldBe 1

    println(s"[DONE] ${cluster.selfMember} join to cluster.")
    cluster.unsubscribe(self)

    // Cluster Sharding のセットアップが終わるまでしばらく待つ
    Thread.sleep(5000)
  }

}
