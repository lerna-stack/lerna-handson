package example

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }

// メッセージを受け取り、コンソールに出力するアクター
object PrintActor {
  def apply(): Behavior[String] =
    Behaviors.receiveMessage { message: String =>
      println(message)
      // 戻り値には 次の Behavior を指定する必要がある
      // 現在と同じ Behavior としたい場合は、 Behaviors.same を指定する
      Behaviors.same
    }
}

object PrintActorExample extends App {
  val behavior: Behavior[String] = PrintActor()

  // ActorSystem[T] を生成する際に Behavior[T] を引数に渡す
  val system: ActorSystem[String] =
    ActorSystem(behavior, "tell-example")

  // ActorRef[T] は T型のメッセージを処理できるアクターへの参照を表す
  // ActorSystem[T] は ActorRef[T] としても扱える
  val actorRef: ActorRef[String] = system

  // アクターにメッセージ "hello" を送る
  // アクターがメッセージを受け取ると "hello" が表示される
  actorRef ! "hello"

  // 次のように T型以外 のメッセージを送ろうとするとコンパイルエラーになる
  // actorRef ! 1

  // アクターがメッセージを処理するまで適当に1秒待って終了する
  Thread.sleep(1000)
  system.terminate()
}
