package example.model.concert.service

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import example.ActorSpecBase
import example.model.ClusterShardingSpecLike

abstract class BoxOfficeServiceSpecBase(actorSystemName: String)
    extends ActorSpecBase(
      ActorSystem(
        actorSystemName,
        ConfigFactory.load("test-akka-cluster"),
      ),
    )
    with ClusterShardingSpecLike
