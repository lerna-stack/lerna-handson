package example.model.concert.actor

import akka.actor.typed.ActorSystem
import akka.cluster.sharding.typed.scaladsl.{ ClusterSharding, Entity, EntityRef, EntityTypeKey }
import akka.persistence.typed.PersistenceId
import example.model.concert.ConcertId
import example.model.concert.actor.ConcertActor

/** ConcertActor の ClusterSharding を管理する
  */
final class ConcertActorClusterSharding(
    system: ActorSystem[Nothing],
    createBehavior: ConcertActorBehaviorFactory,
) {
  private val sharding = ClusterSharding(system)
  private val TypeKey: EntityTypeKey[ConcertActor.Command] =
    EntityTypeKey[ConcertActor.Command]("concerts")

  sharding.init(Entity(TypeKey) { entityContext =>
    val id = ConcertId
      .fromString(entityContext.entityId)
      .left.map(error => new IllegalStateException(error.toString))
      .toTry.get
    val persistenceId = PersistenceId(entityContext.entityTypeKey.name, id.value)
    createBehavior(id, persistenceId)
  })

  def entityRefFor(id: ConcertId): EntityRef[ConcertActor.Command] = {
    sharding.entityRefFor(TypeKey, id.value)
  }
}
