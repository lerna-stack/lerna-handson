package example

import akka.actor.{ ActorRef, ActorSystem, Props }

object ActorInstantiationExample extends App {
  val system: ActorSystem = ActorSystem("my-system")

  // Props はどのようにアクターを生成するか表すファクトリのようなもの
  // new EchoActor() は 名前渡しのため、
  // この時点では EchoActor はインスタンス化されていないことに注意する
  val props: Props = Props(new EchoActor())

  // ここではまだ EchoActor は インスタンス化されていない

  // 次の行で実際に EchoActor がインスタンス化される
  // 得られるものは ActorRef という Actor への参照を表すオブジェクト
  val actorRef: ActorRef = system.actorOf(props)
}
