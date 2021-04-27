package example

import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{ Effect, EventSourcedBehavior }

/** [[DoorActor2]] は [[DoorActor]] と同じ振る舞いを異なるスタイルで記述した例である。
  *
  * 振る舞いは、 [[DoorActor]] と同じである。
  */
object DoorActor2 {

  sealed trait Command
  case object Open  extends Command
  case object Close extends Command

  sealed trait Event
  case object Opened extends Event
  case object Closed extends Event

  // State に Command Handler と Event Handler を定義することもできる
  sealed trait State {
    def applyCommand(command: Command): Effect[Event, State]
    def applyEvent(event: Event): State
  }

  case object ClosedState extends State {
    override def applyCommand(command: Command): Effect[Event, State] = {
      command match {
        case Open  => Effect.persist(Opened).thenRun(println)
        case Close => Effect.none
      }
    }
    override def applyEvent(event: Event): State = {
      event match {
        case Opened => OpenedState
        case Closed => throw new IllegalStateException("unexpected closed event")
      }
    }
  }

  case object OpenedState extends State {
    override def applyCommand(command: Command): Effect[Event, State] = {
      command match {
        case Open  => Effect.none
        case Close => Effect.persist(Closed).thenRun(println)
      }
    }
    override def applyEvent(event: Event): State = {
      event match {
        case Opened => throw new IllegalStateException("unexpected opened event")
        case Closed => ClosedState
      }
    }
  }

  def apply(id: PersistenceId): Behavior[Command] =
    EventSourcedBehavior[Command, Event, State](
      persistenceId = id,
      emptyState = ClosedState,
      commandHandler = (state, command) => state.applyCommand(command),
      eventHandler = (state, event) => state.applyEvent(event),
    )

}

object DoorActor2Example extends App {
  val behavior: Behavior[DoorActor2.Command] =
    DoorActor2(PersistenceId.ofUniqueId("door-actor2-1"))
  val system: ActorSystem[DoorActor2.Command] =
    ActorSystem(behavior, "door-actor2-example")

  // 使用方法は永続化しない場合と同じである
  val actorRef: ActorRef[DoorActor2.Command] = system
  actorRef ! DoorActor2.Open
  actorRef ! DoorActor2.Close

  // アクターがメッセージを処理完了するまで適当に待って終了する
  Thread.sleep(3000)
  system.terminate()
}
