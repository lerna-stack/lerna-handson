package example.model.concert.actor

import java.time.ZonedDateTime
import akka.actor._
import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import example.model.concert.ConcertError._
import example.model.concert.ConcertEvent._
import example.model.concert._
import example.model.concert.actor.ConcertActorProtocol.ConcertCommandRequest

import scala.annotation.nowarn
import scala.concurrent.duration._

object DefaultConcertActorWithEventPersistence extends ConcertActorBehaviorFactory {
  def props(id: ConcertId): Props = Props(new DefaultConcertActorWithEventPersistence(id))

  /** DefaultConcertActorWoPersistence の State を表す。
    */
  sealed trait State extends ActorStateBase[ConcertEvent, State] {
    def toDataModel: ConcertStateData
  }

  override def apply(id: ConcertId, persistenceId: PersistenceId): Behavior[ConcertCommandRequest] = {
    // TODO implement using EventSourcedBehavior and PersistenceID
    ConcertActorBase.createBehavior(props(id))
  }

}

/** 演習途中成果物として参照するとよい。
  * 永続化対応版(イベントのみ)のコード
  *
  * nowarn付与は現在の実装方法ではunchecked警告が解決できないため
  */
@nowarn("cat=unchecked")
final class DefaultConcertActorWithEventPersistence(id: ConcertId)
    extends ConcertActorBase[DefaultConcertActorWithEventPersistence.State] {
  import DefaultConcertActorWithEventPersistence._
  import ConcertActorProtocol._

  // グローバルで一意になるように設定すること
  // 設定済み.変更しないこと
  override def persistenceId: String = "concert-" + id.value

  // Passivateタイムアウトを設定する
  // NOTE: Undefinedではタイムアウトしない
  override protected def passivateTimeout: Duration = Duration.Undefined

  /** 初期状態
    */
  override protected def initialState: State = NoConcertState()

  /** コンサートが存在しない場合
    */
  final case class NoConcertState() extends State {
    override def toDataModel: ConcertStateData = NoConcertStateData()
    override def updated: EventHandler = {
      case created: ConcertCreated =>
        // チケットを作成して、遷移先 AvailableConcertState を返す
        val tickets = (1 to created.numOfTickets).map(ConcertTicketId).toVector
        AvailableConcertState(tickets)
    }
    override def receiveCommand: Receive = {
      case getRequest: GetConcertRequest =>
        // 取得処理, 未作成なのでエラー
        getRequest.replyTo ! GetConcertFailed(ConcertNotFoundError(id))
      case createRequest: CreateConcertRequest =>
        // 作成処理, リクエストに問題がなければ作成イベントを使って状態を拘引する
        if (createRequest.numTickets <= 0) { // チケット枚数が 0以下なので不正
          val error = InvalidConcertOperationError("Cannot create concert without tickets.")
          createRequest.replyTo ! CreateConcertFailed(error)
        } else {
          // 作成イベントを作成する
          val event =
            ConcertCreated(createRequest.concertId, createRequest.numTickets, ZonedDateTime.now)
          persist(event) { _ =>
            updateState(event)
            createRequest.replyTo ! CreateConcertSucceeded(event.numOfTickets)
          }
        }
      case cancelRequest: CancelConcertRequest =>
        // キャンセル処理, 未作成なのでエラー
        cancelRequest.replyTo ! CancelConcertFailed(ConcertNotFoundError(id))
      case buyTicketsRequest: BuyConcertTicketsRequest =>
        // チケット購入処理, 未作成なのでエラー
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
        getRequest.replyTo ! GetConcertSucceeded(id, tickets, cancelled = false)
      case createRequest: CreateConcertRequest =>
        // 既に作成済みなのでエラー
        createRequest.replyTo ! CreateConcertFailed(DuplicatedConcertError(id))
      case cancelRequest: CancelConcertRequest =>
        val event = ConcertCancelled(id, ZonedDateTime.now)
        persist(event) { _ =>
          updateState(event)
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
    // 状態遷移は存在しないので空にする
    override def updated: EventHandler = PartialFunction.empty
    override def receiveCommand: Receive = {
      case getRequest: GetConcertRequest =>
        // 取得処理は成功する
        getRequest.replyTo ! GetConcertSucceeded(id, tickets, cancelled = true)
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

  /** 状態(State)から永続化状態データ(StateData)への変換 (実装済み)
    */
  override protected def toStateData(state: State): ConcertStateData = state.toDataModel

  /** 永続化状態データから状態への変換 (実装済み)
    */
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
