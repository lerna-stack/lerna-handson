package example

import akka.actor._

// Actorトレイトを継承することでアクターを実装できる
final class EchoActor extends Actor {
  // メッセージ受信時の処理を記述する
  override def receive: Receive = {
    // すべてのメッセージを処理する
    case msg: Any =>
      // コンソールにメッセージを表示して、
      // 送信者に 同じメッセージ を送り返す
      println(msg)
      sender() ! msg
  }
}
