package exercise

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import com.typesafe.config.{ Config, ConfigFactory }

/** 演習4: 状態を持つアクター [[CounterActor]] を実装してみよう。
  *
  * (A) 次のようなアクターを実装しよう。
  *   - 任意のInt型数値を受け取り、その数値分カウントアップする
  *     (負数を受け取った場合はカウントダウンになる)
  *   - オーバーフローは気にしなくてよい
  *   - カウンタの初期値は0とする
  *   - メッセージを処理したらすぐに更新後のカウンタ値をコンソールに表示する
  *
  * (B) メッセージを送って、意図通りのカウンタ値がコンソールに表示されるか確認しよう。
  *
  * 解答は [[answer.DefaultCounterActor]] で確認できる。
  */
object CounterActor {
  def apply(): Behavior[Int] = {
    // (A) アクターの定義はここに書こう
    ???
  }
}

object Exercise4 extends App {
  val config: Config =
    ConfigFactory.parseString("akka.log-dead-letters=0")
  val system: ActorSystem[Int] =
    ActorSystem(CounterActor(), "exercise4", config)

  val actorRef: ActorRef[Int] = system
  // (B) ここでメッセージを送ってみよう
  ???

  // アクターがメッセージを処理完了するまで適当に待って終了する
  Thread.sleep(3000)
  system.terminate()
}
