package example

import akka.actor._

object TellExample extends App {
  val system = ActorSystem("tell-example")

  final class EchoActor extends Actor {
    override def receive: Receive = {
      case msg =>
        println((sender(), msg))
        sender() ! msg
    }
  }
  val actorRef: ActorRef = system.actorOf(Props(new EchoActor))

  // 次の2つは同じ意味(アクター外からtellする場合)
  // ! を使う場合には、送信アクターは暗黙的に解決される。
  // tell を使う場合には、明示的に与える必要がある。
  actorRef ! "message"
  actorRef.tell("message", Actor.noSender)

  // アクターが処理完了かわからないので、
  // 適当に1秒くらい待って終了する
  Thread.sleep(1000)
  system.terminate()
}
