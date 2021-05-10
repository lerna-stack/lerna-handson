package exercise

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import com.typesafe.config.{ Config, ConfigFactory }

/** 演習1: [[UpperCasePrintActor]] を実装しよう
  *
  * (A) 次のようなアクターを実装しよう。
  *  - メッセージ(文字列)を大文字に変換しコンソールに表示する。
  *  - 文字列を大文字に変換するには、`String.toUpperCase()` が使える
  *
  * (B) メッセージを送って、大文字に変換されたメッセージがコンソールに表示されることを確認してみよう。
  *
  * 解答は [[answer.DefaultUpperCasePrintActor]] で確認できる。
  */
object UpperCasePrintActor {
  def apply(): Behavior[String] = {
    // (A) アクターの定義はここに書こう
    ???
  }
}

object Exercise1 extends App {
  val config: Config =
    ConfigFactory.parseString("akka.log-dead-letters=0")
  val system: ActorSystem[String] =
    ActorSystem(UpperCasePrintActor(), "exercise1", config)
  val actorRef: ActorRef[String] = system

  // (B) ここでメッセージを送ってみよう
  ???

  // アクターがメッセージを処理完了するまで適当に待って終了する
  Thread.sleep(3000)
  system.terminate()
}
