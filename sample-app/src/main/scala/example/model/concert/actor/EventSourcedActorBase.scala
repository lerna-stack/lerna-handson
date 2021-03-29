package example.model.concert.actor

import akka.actor.ActorLogging
import akka.persistence.{ PersistentActor, RecoveryCompleted, SnapshotOffer }

import scala.reflect.ClassTag

/** EventSourcingのような振る舞いで動かすアクターのベースクラス
  */
abstract class EventSourcedActorBase[Event, State <: ActorStateBase[Event, State], StateData](implicit
    eventClassTag: ClassTag[Event],
    stateDataClassTag: ClassTag[StateData],
) extends PersistentActor
    with ActorLogging {
  private val eventClass     = eventClassTag.runtimeClass.asInstanceOf[Class[Event]]
  private val stateDataClass = stateDataClassTag.runtimeClass.asInstanceOf[Class[StateData]]

  /** 初期状態
    */
  protected def initialState: State

  /** 状態(State)から永続化状態データ(StateData)への変換
    */
  protected def toStateData(state: State): StateData

  /** 永続化状態データから状態への変換
    */
  protected def fromStateData(data: StateData): State

  private var state: State = initialState

  override def receiveCommand: Receive = {
    case msg if state.receiveCommand.isDefinedAt(msg) =>
      state.receiveCommand(msg)
  }

  override def receiveRecover: Receive = {
    case event: Event if eventClass.isInstance(event) =>
      log.info("Recovery from event {}", event)
      updateState(eventClass.cast(event))
    case SnapshotOffer(_, snapshot: Any) if stateDataClass.isInstance(snapshot) =>
      log.info("Recovery from snapshot {}", snapshot)
      state = fromStateData(stateDataClass.cast(snapshot))
    case RecoveryCompleted =>
      log.info("RecoveryCompleted (ID={})", persistenceId)
  }

  /** State を更新する。
    * @param event 適用するイベント
    */
  protected def updateState(event: Event): Unit = {
    state = state.updated.lift.apply(event).getOrElse {
      log.error("Unexpected {} for {}.", event, state)
      state
    }
  }

  /** スナップショットを取得する。
    */
  protected def saveSnapshot(): Unit = {
    saveSnapshot(toStateData(state))
  }
}
