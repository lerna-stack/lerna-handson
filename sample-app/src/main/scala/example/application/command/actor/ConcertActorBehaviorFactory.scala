package example.application.command.actor

import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import example.adapter.ConcertId
import example.application.command.actor.ConcertActor

trait ConcertActorBehaviorFactory {
  def apply(id: ConcertId, persistenceId: PersistenceId): Behavior[ConcertActor.Command]
}
