package exercise

import akka.actor._
import akka.testkit.{ ImplicitSender, TestKit }
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

/*
実装した UpperCaseEchoActor をテストしてみよう。
- 文字列を送信し、大文字に変換されて返信されるかテストする
  expectMsg(...) が使える
- 文字列以外を送信して、返信されないこともテストしよう
  expectNoMessage() が使える
 */
final class Exercise2
    extends TestKit(ActorSystem("exercise2", ConfigFactory.parseString("akka.log-dead-letters=0")))
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll
    with ImplicitSender {
  // 最後に ActorSystem を終了させる
  override def afterAll(): Unit = {
    shutdown(system)
  }

  // ここから下にテストを書く

}
