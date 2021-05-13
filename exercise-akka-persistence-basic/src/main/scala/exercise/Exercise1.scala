package exercise

import akka.actor.typed.scaladsl.AskPattern.{ schedulerFromActorSystem, Askable }
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{ Effect, EventSourcedBehavior }
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ ExecutionContext, Future }

/** [[example.DoorActor]] と同様のアクターを [[MyDoorActor]] で実装してみよう。
  *
  * [[MyDoorActor]] は次の振る舞いをする。
  *
  *  - 2種類のイベント(`Event`)がある
  *    - ドアが開いた(`Opened`)
  *    - ドアが閉じた(`Closed`)
  *  - 2種類の状態(`State`)がある
  *    - 開いている状態(`OpenedState`)
  *    - 閉じている状態(`ClosedState`)
  *  - 3種類のコマンド(`Command`)を処理する
  *    - 開く(`Open`)
  *    - 閉じる(`Close`)
  *    - ドアの開いた回数(`openedCount`)を取得する(`GetOpenedCount`)
  *  - 初期状態は ドアが閉じている状態(`ClosedState`) から始まる
  *  - ドアが開いた場合にのみ `openedCount` を1増やす
  *  - ドアが開いている状態(`OpenedState`)で開くコマンド(`Open`)を受け取った場合や、
  *    ドアが閉じている状態(`ClosedState`)で閉じるコマンド(`Close`)を受け取った場合には、
  *    状態を変更したり、イベントを発行したりしないようにしよう
  *
  * [[example.DoorActor]] と違い、
  * 演習のために、コマンドや状態が幾つか変更されていることに注意しよう。
  *
  * 実装が完了できたら、事前に準備してあるユニットテストにパスすることを確認しよう。
  */
object MyDoorActor {

  /** ドアに発行できるコマンド */
  sealed trait Command

  /** ドアを開く */
  case object Open extends Command

  /** ドアを閉じる */
  case object Close extends Command

  /** ドアが何回開いたかを `replyTo` に返す */
  final case class GetOpenedCount(replyTo: ActorRef[Int]) extends Command

  /** ドアから発行されるイベント */
  sealed trait Event

  /** ドアを開いた */
  case object Opened extends Event

  /** ドアを閉じた */
  case object Closed extends Event

  /** 現在のドアの状態を表す */
  sealed trait State {
    def applyCommand(command: Command): Effect[Event, State]
    def applyEvent(event: Event): State
  }

  /** ドアが閉じている状態 */
  final case class ClosedState(openedCount: Int) extends State {
    override def applyCommand(command: Command): Effect[Event, State] = {
      // ClosedState の Command Handler を実装しよう
      ???
    }
    override def applyEvent(event: Event): State = {
      // ClosedState の Event Handler を実装しよう
      ???
    }
  }

  /** ドアが開いている状態 */
  final case class OpenedState(openedCount: Int) extends State {
    override def applyCommand(command: Command): Effect[Event, State] = {
      // OpenedStateの Command Handler を実装しよう
      ???
    }
    override def applyEvent(event: Event): State = {
      // OpenedState の Event Handler を実装しよう
      ???
    }
  }

  /** [[MyDoorActor]] の [[Behavior]] を作成する */
  def apply(id: PersistenceId): Behavior[Command] =
    EventSourcedBehavior[Command, Event, State](
      persistenceId = id,
      emptyState = ClosedState(0),
      commandHandler = (state, command) => state.applyCommand(command),
      eventHandler = (state, event) => state.applyEvent(event),
    )

}

/** LevelDB を用いて動作確認をするためのアプリケーション
  *
  * ユニットテストにパスすることが確認できたら、
  * [[Exercise1]] を実行して、意図通りに永続化されていることを確認してみよう。
  */
object Exercise1 extends App {
  val behavior: Behavior[MyDoorActor.Command] =
    MyDoorActor(PersistenceId.ofUniqueId("exercise1-actor"))
  implicit val system: ActorSystem[MyDoorActor.Command] =
    ActorSystem(behavior, "exercise1")
  implicit val executionContext: ExecutionContext = system.executionContext
  implicit val askTimeout: Timeout                = 3.seconds

  val actorRef: ActorRef[MyDoorActor.Command] = system

  // ※複数回実行することでイベントが意図通りに永続化されていることも確認できる
  // 1回目の Open
  actorRef ! MyDoorActor.Open
  val f1: Future[Int] = actorRef.ask(replyTo => MyDoorActor.GetOpenedCount(replyTo))
  f1.onComplete(println) // Success(初期値+1) が出力される

  // Close する
  actorRef ! MyDoorActor.Close

  // 2回目の Open
  actorRef ! MyDoorActor.Open
  val f3: Future[Int] = actorRef.ask(replyTo => MyDoorActor.GetOpenedCount(replyTo))
  f3.onComplete(println) // Success(初期値+2)が出力される

  // アクターがメッセージを処理完了するまで適当に待って終了する
  Thread.sleep(3000)
  system.terminate()

}
