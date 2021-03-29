package example

import akka.actor.ActorSystem

object ActorSystemExample extends App {
  // ActorSystemを作成する
  val system: ActorSystem = ActorSystem("my-system")

  // ActorSystem を使って何かする
  // ...

  // ActorSystem を終了させる
  system.terminate()
}
