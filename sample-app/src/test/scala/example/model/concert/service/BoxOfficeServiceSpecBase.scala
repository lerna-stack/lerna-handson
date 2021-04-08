package example.model.concert.service

import com.typesafe.config.ConfigFactory
import example.ActorSpecBase
import example.model.ClusterShardingSpecLike

abstract class BoxOfficeServiceSpecBase()
    extends ActorSpecBase(ConfigFactory.load("test-akka-cluster"))
    with ClusterShardingSpecLike
