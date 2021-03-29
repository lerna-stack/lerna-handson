package example

import akka.actor._
import akka.testkit.{ ImplicitSender, TestKit }
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

final class EchoActorSpec
    extends TestKit(ActorSystem("echo-actor-spec"))
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll
    with ImplicitSender {
  // テストがすべて終わったら、ActorSystem をシャットダウンする
  override def afterAll(): Unit = {
    shutdown(system)
  }

  "EchoActor should echo messages back" in {
    val actorRef = system.actorOf(Props(new EchoActor()))

    // メッセージを送信して、
    // expectMsg でメッセージが送り返されてくることを確認する
    actorRef ! "test"
    expectMsg("test")

    // いくつかのメッセージをまとめて送信して、まとめて確認することもできる
    actorRef ! "Hello"
    actorRef ! "World"
    expectMsg("Hello")
    expectMsg("World")
  }

}
