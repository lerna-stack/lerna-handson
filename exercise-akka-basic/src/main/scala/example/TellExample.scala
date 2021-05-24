package example

import akka.actor.typed.{ ActorRef, ActorSystem }

object TellExample extends App {
  val system: ActorSystem[String] =
    ActorSystem(PrintActor(), "tell-example")

  // PrintActor
  val actorRef: ActorRef[String] = system

  // アクターにメッセージを送るには ! または tell を使う
  // 次の2つは同じ意味である
  actorRef ! "message"
  actorRef.tell("message")

  // アクターがメッセージを処理するまで適当に1秒まってから終了する
  Thread.sleep(1000)
  system.terminate()
}
