package example

import akka.actor.testkit.typed.scaladsl.{ ScalaTestWithActorTestKit, TestProbe }
import akka.actor.typed.ActorRef
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

final class EchoActorSpec extends ScalaTestWithActorTestKit() with AnyWordSpecLike with Matchers {

  "EchoActor should echo messages back" in {
    // テスト対象のアクターを生成する
    val echoActorRef: ActorRef[EchoActor.Message] = spawn(EchoActor())
    // テストで使用するプローブを作成する
    // 型パラメータには返信メッセージの型(String)を指定する
    val probe: TestProbe[String] = createTestProbe[String]()

    // メッセージが送り返されてくることを確認する
    //   probe.ref: ActorRef[String] を使ってメッセージを送信する
    //   返信メッセージは probe.expectMessage で確認できる
    echoActorRef ! EchoActor.Message("test", probe.ref)
    probe.expectMessage("test")

    // いくつかのメッセージをまとめて送信して、まとめて確認することもできる
    echoActorRef ! EchoActor.Message("Hello", probe.ref)
    echoActorRef ! EchoActor.Message("World", probe.ref)
    probe.expectMessage("Hello")
    probe.expectMessage("World")
  }

}
