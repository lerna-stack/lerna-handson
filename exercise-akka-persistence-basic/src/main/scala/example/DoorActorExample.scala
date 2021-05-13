package example

import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{ Effect, EventSourcedBehavior }

/** [[DoorActor]] はドア開閉を [[EventSourcedBehavior]] で記述した例である
  *
  * 複数の Command, Event, State を使用する方法を例示している。
  */
object DoorActor {

  sealed trait Command
  case object Open  extends Command
  case object Close extends Command

  sealed trait Event
  case object Opened extends Event
  case object Closed extends Event

  sealed trait State
  case object ClosedState extends State
  case object OpenedState extends State

  def apply(id: PersistenceId): Behavior[Command] =
    EventSourcedBehavior[Command, Event, State](
      persistenceId = id,
      emptyState = ClosedState,
      commandHandler = commandHandler,
      eventHandler = eventHandler,
    )

  private def commandHandler(state: State, command: Command): Effect[Event, State] = {
    // state と command の組み合わせによって振る舞いが変わる
    (state, command) match {
      case (ClosedState, Open)  => Effect.persist(Opened).thenRun(println)
      case (ClosedState, Close) => Effect.none
      case (OpenedState, Open)  => Effect.none
      case (OpenedState, Close) => Effect.persist(Closed).thenRun(println)
    }
  }

  private def eventHandler(state: State, event: Event): State = {
    // state と event の組み合わせによって次の状態が変わる
    (state, event) match {
      case (ClosedState, Opened) => OpenedState
      case (OpenedState, Closed) => ClosedState
      case invalid               => throw new IllegalStateException(s"Unexpected $invalid")
    }
  }

}

object DoorActorExample extends App {
  val behavior: Behavior[DoorActor.Command] =
    DoorActor(PersistenceId.ofUniqueId("door-actor-1"))
  val system: ActorSystem[DoorActor.Command] =
    ActorSystem(behavior, "door-actor-example")

  // 使用方法は永続化しない場合と同じである
  val actorRef: ActorRef[DoorActor.Command] = system
  actorRef ! DoorActor.Open
  actorRef ! DoorActor.Close

  // アクターがメッセージを処理完了するまで適当に待って終了する
  Thread.sleep(3000)
  system.terminate()
}
