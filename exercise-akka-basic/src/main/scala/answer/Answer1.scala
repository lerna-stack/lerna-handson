package answer

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }

object DefaultUpperCasePrintActor {
  def apply(): Behavior[String] = {
    // (A) アクターの定義はここに書こう
    Behaviors.receiveMessage { message: String =>
      println(message.toUpperCase)
      Behaviors.same
    }
  }
}

object Answer1 extends App {
  val system: ActorSystem[String] =
    ActorSystem(DefaultUpperCasePrintActor(), "answer1")
  val actorRef: ActorRef[String] = system

  // (B) ここでメッセージを送ってみよう
  actorRef ! "hello" // => HELLO
  actorRef ! "world" // => WORLD

  // アクターがメッセージを処理完了するまで適当に待って終了する
  Thread.sleep(3000)
  system.terminate()
}
