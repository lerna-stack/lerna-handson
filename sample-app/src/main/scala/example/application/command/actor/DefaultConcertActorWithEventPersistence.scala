package example.application.command.actor

import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{ Effect, EventSourcedBehavior }
import example.adapter._
import example.adapter.ConcertError._
import example.application.ConcertEvent._
import example.application.{ ConcertEvent, KryoSerializable }
import example.application.command.actor.ConcertActor._

import java.time.ZonedDateTime

object DefaultConcertActorWithEventPersistence extends ConcertActorBehaviorFactory {
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
    override def applyCommand(command: Command): ReplyEffect = {
      command match {
        case getCommand: Get =>
          // 未作成なのでエラー
          Effect.reply(getCommand.replyTo)(GetFailed(ConcertNotFoundError(id)))
        case createCommand: Create =>
          // 作成イベントを発行する処理
          if (createCommand.numTickets <= 0) {
            // チケット数が0枚以下なのでエラー
            val error = InvalidConcertOperationError("Cannot create concert without tickets.")
            Effect.reply(createCommand.replyTo)(CreateFailed(error))
          } else {
            // 作成成功
            val event = ConcertCreated(id, createCommand.numTickets, ZonedDateTime.now)
            Effect.persist(event).thenReply(createCommand.replyTo)(_ => CreateSucceeded(event.numOfTickets))
          }
        case cancelCommand: Cancel =>
          // 未作成なのでエラー
          Effect.reply(cancelCommand.replyTo)(CancelFailed(ConcertNotFoundError(id)))
        case buyTicketsCommand: BuyTickets =>
          // 未作成なのでエラー
          Effect.reply(buyTicketsCommand.replyTo)(BuyTicketsFailed(ConcertNotFoundError(id)))
      }
    }
    override def applyEvent(event: ConcertEvent): State = {
      event match {
        case created: ConcertCreated =>
          // 作成イベントで AvailableConcertState に状態遷移する
          val tickets = (1 to created.numOfTickets).map(ConcertTicketId).toVector
          AvailableConcertState(id, tickets)
        case _ =>
          throw new IllegalStateException(s"unexpected event [$event] in state [${this.getClass.getSimpleName}]")
      }
    }
  }

  /** コンサートが存在する場合(未キャンセル) */
  final case class AvailableConcertState(id: ConcertId, tickets: Vector[ConcertTicketId]) extends State {
    override def applyCommand(command: Command): ReplyEffect = {
      command match {
        case getCommand: Get =>
          Effect.reply(getCommand.replyTo)(GetSucceeded(tickets, cancelled = false))
        case createCommand: Create =>
          // 既に作成済みなのでエラー
          Effect.reply(createCommand.replyTo)(CreateFailed(DuplicatedConcertError(id)))
        case cancelCommand: Cancel =>
          // キャンセル処理を実行する
          val event = ConcertCancelled(id, ZonedDateTime.now)
          Effect.persist(event).thenReply(cancelCommand.replyTo)(_ => CancelSucceeded(tickets.size))
        case buyTicketsCommand: BuyTickets =>
          // チケット購入処理
          if (buyTicketsCommand.numberOfTickets <= 0) {
            // チケット枚数がゼロ以下なのでエラー
            val error = InvalidConcertOperationError("Cannot a buy non positive number of tickets.")
            Effect.reply(buyTicketsCommand.replyTo)(BuyTicketsFailed(error))
          } else if (buyTicketsCommand.numberOfTickets > tickets.size) {
            // 残チケット枚数が不足しているのでエラー
            val error = InvalidConcertOperationError("Not enough tickets available.")
            Effect.reply(buyTicketsCommand.replyTo)(BuyTicketsFailed(error))
          } else {
            val boughtTickets = tickets.take(buyTicketsCommand.numberOfTickets)
            val event         = ConcertTicketsBought(id, boughtTickets, ZonedDateTime.now)
            Effect.persist(event).thenReply(buyTicketsCommand.replyTo)(_ => BuyTicketsSucceeded(event.tickets))
          }
      }
    }
    override def applyEvent(event: ConcertEvent): State = {
      event match {
        case _: ConcertCancelled =>
          // キャンセル状態に遷移する
          CancelledConcertState(id, tickets)
        case ticketsBoughtEvent: ConcertTicketsBought =>
          // チケットを減らして自身と同じ状態に遷移する
          val remainingTickets = tickets.filterNot(ticketsBoughtEvent.tickets.contains)
          AvailableConcertState(id, remainingTickets)
        case _ =>
          throw new IllegalStateException(s"unexpected event [$event] in state [${this.getClass.getSimpleName}]")
      }
    }
  }

  /** コンサートが存在する場合(キャンセル済み)
    */
  final case class CancelledConcertState(id: ConcertId, tickets: Vector[ConcertTicketId]) extends State {
    override def applyCommand(command: Command): ReplyEffect = {
      command match {
        case getCommand: Get =>
          // 取得処理は成功する
          Effect.reply(getCommand.replyTo)(GetSucceeded(tickets, cancelled = true))
        case createCommand: Create =>
          // すでにコンサートが存在するのでエラー
          Effect.reply(createCommand.replyTo)(CreateFailed(DuplicatedConcertError(id)))
        case cancelCommand: Cancel =>
          // すでにキャンセル済みなのでエラー
          val error = InvalidConcertOperationError("Concert is already cancelled.")
          Effect.reply(cancelCommand.replyTo)(CancelFailed(error))
        case buyTicketsCommand: BuyTickets =>
          // すでにキャンセル済みなのでエラー
          val error = InvalidConcertOperationError("Concert is already cancelled.")
          Effect.reply(buyTicketsCommand.replyTo)(BuyTicketsFailed(error))
      }
    }
    override def applyEvent(event: ConcertEvent): State = {
      throw new IllegalStateException(s"unexpected event [$event] in state [${this.getClass.getSimpleName}]")
    }
  }
}
