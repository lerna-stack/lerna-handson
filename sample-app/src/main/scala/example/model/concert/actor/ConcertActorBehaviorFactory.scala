package example.model.concert.actor

import akka.actor.typed.Behavior
import example.model.concert.ConcertId
import example.model.concert.actor.ConcertActorProtocol.ConcertCommandRequest

trait ConcertActorBehaviorFactory {
  def apply(id: ConcertId): Behavior[ConcertCommandRequest]
}
