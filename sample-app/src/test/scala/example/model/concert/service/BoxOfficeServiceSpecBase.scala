package example.model.concert.service

import example.ActorSpecBase
import example.model.ClusterShardingSpecLike

abstract class BoxOfficeServiceSpecBase() extends ActorSpecBase() with ClusterShardingSpecLike
