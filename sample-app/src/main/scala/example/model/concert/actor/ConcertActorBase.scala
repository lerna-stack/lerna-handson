package example.model.concert.actor

import akka.actor.typed.{ ActorRef, ActorSystem }
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.scaladsl.{ ClusterSharding, Entity, EntityRef, EntityTypeKey }
import akka.persistence.typed.PersistenceId
import example.model.concert._
import example.model.concert.actor.ConcertActorProtocol.ConcertCommandRequest

object ConcertActorBase {

  /** ShardedConcertActor の ClusterSharding を開始する。
    */
  def startClusterSharding(
      system: ActorSystem[Nothing],
      config: ConcertActorConfig,
      createBehavior: ConcertActorBehaviorFactory,
  ): ConcertActorClusterSharding = {
    new ConcertActorClusterSharding(system, config, createBehavior)
  }

  /** ConcertActor の ClusterSharding の情報を保持するクラス
    */
  final class ConcertActorClusterSharding(
      system: ActorSystem[Nothing],
      config: ConcertActorConfig,
      createBehavior: ConcertActorBehaviorFactory,
  ) {
    private val sharding                                      = ClusterSharding(system)
    private val TypeKey: EntityTypeKey[ConcertCommandRequest] = EntityTypeKey[ConcertCommandRequest](config.shardName)

    /** ShardRegion を返す
      */
    val shardRegion: ActorRef[ShardingEnvelope[ConcertCommandRequest]] =
      sharding.init(Entity(TypeKey) { entityContext =>
        val id = ConcertId
          .fromString(entityContext.entityId)
          .left.map(error => new IllegalStateException(error.toString))
          .toTry.get
        val persistenceId = PersistenceId(entityContext.entityTypeKey.name, id.value)
        createBehavior(id, persistenceId)
      })

    def entityRefFor(id: ConcertId): EntityRef[ConcertCommandRequest] = {
      sharding.entityRefFor(TypeKey, id.value)
    }
  }

}
