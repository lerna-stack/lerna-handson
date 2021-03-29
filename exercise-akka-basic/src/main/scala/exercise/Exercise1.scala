package exercise

import akka.actor._
import com.typesafe.config.{ Config, ConfigFactory }

/*
(A) 次のようなアクターを実装してみよう。
- メッセージ(文字列)を大文字に変換し
  - コンソールに出力
  - 送信者に返送する
- 文字列を大文字に変換するには、“something”.toUpperCase が使える
- 文字列以外は処理しない

(B) アクターを生成し、メッセージを送ってみよう
- 文字列を送ってみよう
- 数値を送ってみよう
 */

// アクターの定義はここに書こう

object Exercise1 extends App {
  val config: Config      = ConfigFactory.parseString("akka.log-dead-letters=0")
  val system: ActorSystem = ActorSystem("exercise1", config)

  // ここでアクターを作成し、メッセージを送ってみよう

  // 3秒待って終了する
  Thread.sleep(3000)
  system.terminate()
}
