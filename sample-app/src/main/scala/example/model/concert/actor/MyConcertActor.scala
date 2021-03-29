package example.model.concert.actor

import java.time.ZonedDateTime

import akka.actor.Props
import example.model.concert.ConcertError._
import example.model.concert.ConcertEvent._
import example.model.concert._

import scala.annotation.nowarn
import scala.concurrent.duration._

object MyConcertActor {
  def props: Props = Props(new MyConcertActor)

  /** MyConcertActor の State を表す。
    */
  sealed trait State extends ActorStateBase[ConcertEvent, State] {
    def toDataModel: ConcertStateData
  }
}

// 現在の実装方法ではunchecked警告が解決できないため
@nowarn("cat=unchecked")
final class MyConcertActor extends ConcertActorBase[MyConcertActor.State] {
  import MyConcertActor._
  import ConcertActorProtocol._

  // グローバルで一意になるように設定すること
  // 設定済み.変更しないこと
  override def persistenceId: String = "concert-" + id.value

  // Passivateタイムアウトを設定する
  // NOTE: Undefinedではタイムアウトしない
  override protected def passivateTimeout: Duration = Duration.Undefined

  /** 初期状態
    */
  override protected def initialState: State = ???

  /** コンサートが存在しない場合
    */
  final case class NoConcertState() extends State {
    override def toDataModel: ConcertStateData = NoConcertStateData()
    override def updated: EventHandler         = ???
    override def receiveCommand: Receive       = ???
  }

  /** コンサートが存在する場合(未キャンセル)
    */
  final case class AvailableConcertState(tickets: Vector[ConcertTicketId]) extends State {
    override def toDataModel: ConcertStateData = AvailableConcertStateData(tickets)
    override def updated: EventHandler         = ???
    override def receiveCommand: Receive       = ???
  }

  /** コンサートが存在する場合(キャンセル済み)
    */
  final case class CancelledConcertState(tickets: Vector[ConcertTicketId]) extends State {
    override def toDataModel: ConcertStateData = CancelledConcertStateData(tickets)
    override def updated: EventHandler         = ???
    override def receiveCommand: Receive       = ???
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
