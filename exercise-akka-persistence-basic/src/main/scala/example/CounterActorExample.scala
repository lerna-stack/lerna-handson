package example

import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{ Effect, EventSourcedBehavior }

/** カウンタアクター (永続化版)
  *  - [[EventSourcedBehavior]] を使用する。
  *  - [[EventSourcedBehavior]] には、Command, Event, State の 3 つが必要である。
  *  - [[CounterActor.Increment]] を受け取ると カウンタを +1 する。
  *  - イベントはデータストア LevelDB に永続化される。
  */
object CounterActor {

  sealed trait Command
  case object Increment extends Command

  sealed trait Event
  case object Incremented extends Event

  final case class State(count: Int)

  def apply(id: PersistenceId): Behavior[Command] =
    EventSourcedBehavior[Command, Event, State](
      persistenceId = id,
      emptyState = State(0),
      commandHandler = commandHandler,
      eventHandler = eventHandler,
    )

  // 現在の State と 受信した Command をもとに、永続化などの振る舞い Effect を構築する
  private def commandHandler(state: State, command: Command): Effect[Event, State] = {
    command match {
      case Increment =>
        Effect
          .persist(Incremented)
          .thenRun { newState: State =>
            println(newState.count)
          }
    }
  }

  // 現在の State と 永続化した Event をもとに、次の State を作成する
  private def eventHandler(state: State, event: Event): State = {
    event match {
      case Incremented =>
        val newCount: Int = state.count + 1
        State(newCount)
    }
  }

}

object CounterActorExample extends App {
  val behavior: Behavior[CounterActor.Command] =
    CounterActor(PersistenceId.ofUniqueId("counter-actor-1"))
  val system: ActorSystem[CounterActor.Command] =
    ActorSystem(behavior, "counter-actor-example")

  // 使用方法は永続化しない場合と同じである
  // 何度か実行してみると、カウンタ値が増加し続けていくことが確認できる
  val actorRef: ActorRef[CounterActor.Command] = system
  actorRef ! CounterActor.Increment
  actorRef ! CounterActor.Increment
  actorRef ! CounterActor.Increment

  // アクターがメッセージを処理完了するまで適当に待って終了する
  Thread.sleep(3000)
  system.terminate()
}
