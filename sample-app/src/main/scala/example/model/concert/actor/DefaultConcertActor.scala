package example.model.concert.actor

import akka.actor.Props
import akka.actor.typed.Behavior
import example.model.concert.ConcertError._
import example.model.concert._
import example.model.concert.actor.ConcertActorProtocol.ConcertCommandRequest

import java.time.ZonedDateTime
import scala.annotation.nowarn
import scala.concurrent.duration._

object DefaultConcertActor extends ConcertActorBehaviorFactory {
  def props(id: ConcertId): Props = Props(new DefaultConcertActor(id))

  /** DefaultConcertActor の State を表す。
    */
  sealed trait State extends ActorStateBase[ConcertEvent, State] {
    def toDataModel: ConcertStateData
  }

  def apply(id: ConcertId): Behavior[ConcertCommandRequest] = {
    ConcertActorBase.createBehavior(props(id))
  }

}

// 現在の実装方法ではunchecked警告が解決できないため
@nowarn("cat=unchecked")
final class DefaultConcertActor(id: ConcertId) extends ConcertActorBase[DefaultConcertActor.State] {
  import ConcertActorProtocol._
  import ConcertEvent._
  import DefaultConcertActor._

  override def persistenceId: String = "concert-" + id.value
  protected def initialState: State  = NoConcertState()

  // Passivateタイムアウトを設定する
  override protected def passivateTimeout: Duration = 10.seconds

  /** コンサートが存在しない場合
    */
  final case class NoConcertState() extends State {
    override def toDataModel: ConcertStateData = NoConcertStateData()
    override def updated: EventHandler = {
      case created: ConcertCreated =>
        // 作成イベントで AvailableConcertState に状態遷移する
        val tickets = (1 to created.numOfTickets).map(ConcertTicketId).toVector
        AvailableConcertState(tickets)
    }
    override def receiveCommand: Receive = {
      case getRequest: GetConcertRequest =>
        // 未作成なのでエラー
        getRequest.replyTo ! GetConcertFailed(ConcertNotFoundError(id))
      case createRequest: CreateConcertRequest =>
        // 作成イベントを発行する処理
        if (createRequest.numTickets <= 0) {
          // チケット数が0枚以下なのでエラー
          val error = InvalidConcertOperationError("Cannot create concert without tickets.")
          createRequest.replyTo ! CreateConcertFailed(error)
        } else {
          // 作成成功
          val event = ConcertCreated(createRequest.concertId, createRequest.numTickets, ZonedDateTime.now)
          persist(event) { _ =>
            updateState(event)
            createRequest.replyTo ! CreateConcertSucceeded(event.numOfTickets)
          }
        }
      case cancelRequest: CancelConcertRequest =>
        // 未作成なのでエラー
        cancelRequest.replyTo ! CancelConcertFailed(ConcertNotFoundError(id))
      case buyTicketsRequest: BuyConcertTicketsRequest =>
        // 未作成なのでエラー
        buyTicketsRequest.replyTo ! BuyConcertTicketsFailed(ConcertNotFoundError(id))
    }
  }

  /** コンサートが存在する場合(未キャンセル)
    */
  final case class AvailableConcertState(tickets: Vector[ConcertTicketId]) extends State {
    override def toDataModel: ConcertStateData = AvailableConcertStateData(tickets)
    override def updated: EventHandler = {
      case _: ConcertCancelled =>
        // キャンセル状態に遷移する
        CancelledConcertState(tickets)
      case ticketsBoughtEvent: ConcertTicketsBought =>
        // チケットを減らして自身と同じ状態に遷移する
        val remainingTickets = tickets.filterNot(ticketsBoughtEvent.tickets.contains)
        AvailableConcertState(remainingTickets)
    }
    override def receiveCommand: Receive = {
      case getRequest: GetConcertRequest =>
        getRequest.replyTo ! GetConcertSucceeded(id, tickets, false)
      case createRequest: CreateConcertRequest =>
        // 既に作成済みなのでエラー
        createRequest.replyTo ! CreateConcertFailed(DuplicatedConcertError(id))
      case cancelRequest: CancelConcertRequest =>
        // キャンセル処理を実行する
        val event = ConcertCancelled(id, ZonedDateTime.now)
        persist(event) { _ =>
          updateState(event)
          saveSnapshot()
          cancelRequest.replyTo ! CancelConcertSucceeded(tickets.size)
        }
      case buyTicketsRequest: BuyConcertTicketsRequest =>
        // チケット購入処理
        if (buyTicketsRequest.numberOfTickets <= 0) {
          // チケット枚数がゼロ以下なのでエラー
          val error = InvalidConcertOperationError("Cannot a buy non positive number of tickets.")
          buyTicketsRequest.replyTo ! BuyConcertTicketsFailed(error)
        } else if (buyTicketsRequest.numberOfTickets > tickets.size) {
          // 残チケット枚数が不足しているのでエラー
          val error = InvalidConcertOperationError("Not enough tickets available.")
          buyTicketsRequest.replyTo ! BuyConcertTicketsFailed(error)
        } else {
          val boughtTickets = tickets.take(buyTicketsRequest.numberOfTickets)
          val event         = ConcertTicketsBought(id, boughtTickets, ZonedDateTime.now)
          persist(event) { _ =>
            updateState(event)
            buyTicketsRequest.replyTo ! BuyConcertTicketsSucceeded(event.tickets)
          }
        }
    }
  }

  /** コンサートが存在する場合(キャンセル済み)
    */
  final case class CancelledConcertState(tickets: Vector[ConcertTicketId]) extends State {
    override def toDataModel: ConcertStateData = CancelledConcertStateData(tickets)
    override def updated: EventHandler         = PartialFunction.empty
    override def receiveCommand: Receive = {
      case getRequest: GetConcertRequest =>
        // 取得処理は成功する
        getRequest.replyTo ! GetConcertSucceeded(id, tickets, true)
      case createRequest: CreateConcertRequest =>
        // すでにコンサートが存在するのでエラー
        createRequest.replyTo ! CreateConcertFailed(DuplicatedConcertError(id))
      case cancelRequest: CancelConcertRequest =>
        // すでにキャンセル済みなのでエラー
        val error = InvalidConcertOperationError("Concert is already cancelled.")
        cancelRequest.replyTo ! CancelConcertFailed(error)
      case buyTicketsRequest: BuyConcertTicketsRequest =>
        // すでにキャンセル済みなのでエラー
        val error = InvalidConcertOperationError("Concert is already cancelled.")
        buyTicketsRequest.replyTo ! BuyConcertTicketsFailed(error)
    }
  }

  override protected def toStateData(state: State): ConcertStateData = state.toDataModel

  override protected def fromStateData(data: ConcertStateData): State = {
    data match {
      case _: NoConcertStateData =>
        NoConcertState()
      case available: AvailableConcertStateData =>
        AvailableConcertState(available.tickets)
      case cancelled: CancelledConcertStateData =>
        CancelledConcertState(cancelled.tickets)
    }
  }
}
