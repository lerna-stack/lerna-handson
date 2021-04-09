package example.model.concert.actor

import akka.actor.typed.ActorSystem
import akka.cluster.sharding.typed.scaladsl.{ ClusterSharding, Entity, EntityRef, EntityTypeKey }
import akka.persistence.typed.PersistenceId
import example.model.concert.ConcertId
import example.model.concert.actor.ConcertActorProtocol.ConcertCommandRequest

object ConcertActorClusterSharding {

  /** ShardedConcertActor の ClusterSharding を開始する。
    */
  def apply(system: ActorSystem[Nothing], behaviorFactory: ConcertActorBehaviorFactory): ConcertActorClusterSharding = {
    val settings = ConcertActorClusterShardingSettings(system)
    new ConcertActorClusterSharding(system, settings, behaviorFactory)
  }

}

/** ConcertActor の ClusterSharding の情報を保持するクラス
  */
final class ConcertActorClusterSharding(
    system: ActorSystem[Nothing],
    settings: ConcertActorClusterShardingSettings,
    createBehavior: ConcertActorBehaviorFactory,
) {
  private val sharding = ClusterSharding(system)
  private val TypeKey: EntityTypeKey[ConcertCommandRequest] =
    EntityTypeKey[ConcertCommandRequest](settings.entityTypeKeyName)

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
