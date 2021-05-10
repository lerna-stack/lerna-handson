package answer

import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.actor.typed.scaladsl.Behaviors
import com.typesafe.config.{ Config, ConfigFactory }

object DefaultCounterActor {
  def apply(): Behavior[Int] = {
    // (A) アクターの定義はここに書こう
    apply(0)
  }
  private def apply(count: Int): Behavior[Int] = {
    Behaviors.receiveMessage { delta: Int =>
      val newCount: Int = count + delta
      println(newCount)
      apply(newCount)
    }
  }
}

object Answer4 extends App {
  val config: Config =
    ConfigFactory.parseString("akka.log-dead-letters=0")
  val system: ActorSystem[Int] =
    ActorSystem(DefaultCounterActor(), "answer4", config)

  val actorRef: ActorRef[Int] = system
  // (B) ここでメッセージを送ってみよう
  actorRef ! +2
  actorRef ! -3
  actorRef ! +5
  // 2
  // -1
  // 4
  // が表示される

  // アクターがメッセージを処理完了するまで適当に待って終了する
  Thread.sleep(3000)
  system.terminate()
}
