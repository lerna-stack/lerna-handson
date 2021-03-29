package example

import akka.actor._

object EchoActorExample extends App {
  // ActorSystemを作成する
  val system: ActorSystem = ActorSystem("my-system")

  // EchoActorを生成する
  val props: Props       = Props(new EchoActor())
  val actorRef: ActorRef = system.actorOf(props)

  // アクターへの参照にメッセージを送信する
  actorRef ! "Hello World!"
  actorRef ! 1234

  // アクターがメッセージを処理するまで適当に1秒程度待つ
  Thread.sleep(1000)

  // ActorSystem を終了させる
  system.terminate()
}
