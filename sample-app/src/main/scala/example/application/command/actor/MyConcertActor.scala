package example.application.command.actor

import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl._
import example.adapter._
import example.adapter.ConcertError._
import example.application.ConcertEvent._
import example.application.{ ConcertEvent, KryoSerializable }
import example.application.command.actor.ConcertActor._

import java.time.ZonedDateTime

object MyConcertActor extends ConcertActorBehaviorFactory {
  def apply(id: ConcertId, persistenceId: PersistenceId): Behavior[Command] = {
    EventSourcedBehavior
      .withEnforcedReplies[Command, ConcertEvent, State](
        persistenceId,
        emptyState = NoConcertState(id),
        (state, command) => state.applyCommand(command),
        (state, event) => state.applyEvent(event),
      )
      .withTagger(_ => Set(ConcertEvent.tag))
  }

  type ReplyEffect = akka.persistence.typed.scaladsl.ReplyEffect[ConcertEvent, State]
  sealed trait State extends KryoSerializable {
    def applyCommand(command: Command): ReplyEffect
    def applyEvent(event: ConcertEvent): State
  }

  /** コンサートが存在しない場合 */
  final case class NoConcertState(id: ConcertId) extends State {
    override def applyCommand(command: Command): ReplyEffect = ???
    override def applyEvent(event: ConcertEvent): State      = ???
  }

  /** コンサートが存在する場合(未キャンセル) */
  final case class AvailableConcertState(id: ConcertId, tickets: Vector[ConcertTicketId]) extends State {
    override def applyCommand(command: Command): ReplyEffect = ???
    override def applyEvent(event: ConcertEvent): State      = ???
  }

  /** コンサートが存在する場合(キャンセル済み) */
  final case class CancelledConcertState(id: ConcertId, tickets: Vector[ConcertTicketId]) extends State {
    override def applyCommand(command: Command): ReplyEffect = ???
    override def applyEvent(event: ConcertEvent): State      = ???
  }
}
