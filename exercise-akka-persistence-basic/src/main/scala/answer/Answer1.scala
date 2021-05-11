package answer

import akka.actor.typed.scaladsl.AskPattern.{ schedulerFromActorSystem, Askable }
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{ Effect, EventSourcedBehavior }
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ ExecutionContext, Future }

object DefaultDoorActor {

  sealed trait Command
  case object Open                                        extends Command
  case object Close                                       extends Command
  final case class GetOpenedCount(replyTo: ActorRef[Int]) extends Command

  sealed trait Event
  case object Opened extends Event
  case object Closed extends Event

  sealed trait State {
    def applyCommand(command: Command): Effect[Event, State]
    def applyEvent(event: Event): State
  }

  final case class ClosedState(openedCount: Int) extends State {
    override def applyCommand(command: Command): Effect[Event, State] = {
      command match {
        case Open                    => Effect.persist(Opened)
        case Close                   => Effect.none
        case GetOpenedCount(replyTo) => Effect.reply(replyTo)(openedCount)
      }
    }
    override def applyEvent(event: Event): State = {
      event match {
        case Opened => OpenedState(openedCount + 1)
        case Closed => throw new IllegalStateException("Unexpected Closed")
      }
    }
  }

  final case class OpenedState(openedCount: Int) extends State {
    override def applyCommand(command: Command): Effect[Event, State] = {
      command match {
        case Open                    => Effect.none
        case Close                   => Effect.persist(Closed)
        case GetOpenedCount(replyTo) => Effect.reply(replyTo)(openedCount)
      }
    }
    override def applyEvent(event: Event): State = {
      event match {
        case Opened => throw new IllegalStateException("Unexpected Opened")
        case Closed => ClosedState(openedCount)
      }
    }
  }

  def apply(id: PersistenceId): Behavior[Command] =
    EventSourcedBehavior[Command, Event, State](
      persistenceId = id,
      emptyState = ClosedState(0),
      commandHandler = (state, command) => state.applyCommand(command),
      eventHandler = (state, event) => state.applyEvent(event),
    )

}

object Answer1 extends App {
  val behavior: Behavior[DefaultDoorActor.Command] =
    DefaultDoorActor(PersistenceId.ofUniqueId("answer1-actor"))
  implicit val system: ActorSystem[DefaultDoorActor.Command] =
    ActorSystem(behavior, "answer1")
  implicit val executionContext: ExecutionContext = system.executionContext
  implicit val askTimeout: Timeout                = 3.seconds

  val actorRef: ActorRef[DefaultDoorActor.Command] = system

  // ※複数回実行することでイベントが意図通りに永続化されていることも確認できる
  // 1回目の Open
  actorRef ! DefaultDoorActor.Open
  val f1: Future[Int] = actorRef.ask(replyTo => DefaultDoorActor.GetOpenedCount(replyTo))
  f1.onComplete(println) // Success(初期値+1) が出力される

  // Close する
  actorRef ! DefaultDoorActor.Close

  // 2回目の Open
  actorRef ! DefaultDoorActor.Open
  val f3: Future[Int] = actorRef.ask(replyTo => DefaultDoorActor.GetOpenedCount(replyTo))
  f3.onComplete(println) // Success(初期値+2)が出力される

  // アクターがメッセージを処理完了するまで適当に待って終了する
  Thread.sleep(3000)
  system.terminate()

}
