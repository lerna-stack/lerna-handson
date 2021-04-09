package example.model.concert.actor

import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{ Effect, EventSourcedBehavior }
import example.model.KryoSerializable
import example.model.concert.ConcertError._
import example.model.concert.ConcertEvent.{ ConcertCancelled, ConcertCreated, ConcertTicketsBought }
import example.model.concert._
import example.model.concert.actor.ConcertActor._

import java.time.ZonedDateTime

object DefaultConcertActor extends ConcertActorBehaviorFactory {
  def apply(id: ConcertId, persistenceId: PersistenceId): Behavior[Command] = {
    EventSourcedBehavior
      .withEnforcedReplies[Command, ConcertEvent, State](
        persistenceId,
        emptyState = NoConcertState(id),
        (state, command) => state.applyCommand(command),
        (state, event) => state.applyEvent(event),
      )
      .withTagger(_ => Set(ConcertEvent.tag))
      .snapshotWhen {
        case (_, _: ConcertCancelled, _) => true
        case _                           => false
      }
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
        case getRequest: Get =>
          // 未作成なのでエラー
          Effect.reply(getRequest.replyTo)(GetConcertFailed(ConcertNotFoundError(id)))
        case createRequest: Create =>
          // 作成イベントを発行する処理
          if (createRequest.numTickets <= 0) {
            // チケット数が0枚以下なのでエラー
            val error = InvalidConcertOperationError("Cannot create concert without tickets.")
            Effect.reply(createRequest.replyTo)(CreateConcertFailed(error))
          } else {
            // 作成成功
            val event = ConcertCreated(id, createRequest.numTickets, ZonedDateTime.now)
            Effect.persist(event).thenReply(createRequest.replyTo)(_ => CreateConcertSucceeded(event.numOfTickets))
          }
        case cancelRequest: Cancel =>
          // 未作成なのでエラー
          Effect.reply(cancelRequest.replyTo)(CancelConcertFailed(ConcertNotFoundError(id)))
        case buyTicketsRequest: BuyTickets =>
          // 未作成なのでエラー
          Effect.reply(buyTicketsRequest.replyTo)(BuyConcertTicketsFailed(ConcertNotFoundError(id)))
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
        case getRequest: Get =>
          Effect.reply(getRequest.replyTo)(GetConcertSucceeded(tickets, cancelled = false))
        case createRequest: Create =>
          // 既に作成済みなのでエラー
          Effect.reply(createRequest.replyTo)(CreateConcertFailed(DuplicatedConcertError(id)))
        case cancelRequest: Cancel =>
          // キャンセル処理を実行する
          val event = ConcertCancelled(id, ZonedDateTime.now)
          Effect.persist(event).thenReply(cancelRequest.replyTo)(_ => CancelConcertSucceeded(tickets.size))
        case buyTicketsRequest: BuyTickets =>
          // チケット購入処理
          if (buyTicketsRequest.numberOfTickets <= 0) {
            // チケット枚数がゼロ以下なのでエラー
            val error = InvalidConcertOperationError("Cannot a buy non positive number of tickets.")
            Effect.reply(buyTicketsRequest.replyTo)(BuyConcertTicketsFailed(error))
          } else if (buyTicketsRequest.numberOfTickets > tickets.size) {
            // 残チケット枚数が不足しているのでエラー
            val error = InvalidConcertOperationError("Not enough tickets available.")
            Effect.reply(buyTicketsRequest.replyTo)(BuyConcertTicketsFailed(error))
          } else {
            val boughtTickets = tickets.take(buyTicketsRequest.numberOfTickets)
            val event         = ConcertTicketsBought(id, boughtTickets, ZonedDateTime.now)
            Effect.persist(event).thenReply(buyTicketsRequest.replyTo)(_ => BuyConcertTicketsSucceeded(event.tickets))
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
        case getRequest: Get =>
          // 取得処理は成功する
          Effect.reply(getRequest.replyTo)(GetConcertSucceeded(tickets, cancelled = true))
        case createRequest: Create =>
          // すでにコンサートが存在するのでエラー
          Effect.reply(createRequest.replyTo)(CreateConcertFailed(DuplicatedConcertError(id)))
        case cancelRequest: Cancel =>
          // すでにキャンセル済みなのでエラー
          val error = InvalidConcertOperationError("Concert is already cancelled.")
          Effect.reply(cancelRequest.replyTo)(CancelConcertFailed(error))
        case buyTicketsRequest: BuyTickets =>
          // すでにキャンセル済みなのでエラー
          val error = InvalidConcertOperationError("Concert is already cancelled.")
          Effect.reply(buyTicketsRequest.replyTo)(BuyConcertTicketsFailed(error))
      }
    }
    override def applyEvent(event: ConcertEvent): State = {
      throw new IllegalStateException(s"unexpected event [$event] in state [${this.getClass.getSimpleName}]")
    }
  }

}
