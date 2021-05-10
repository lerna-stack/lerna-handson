package example

import akka.actor.typed.scaladsl.AskPattern.{ schedulerFromActorSystem, Askable }
import akka.actor.typed.{ ActorRef, ActorSystem }
import akka.util._

import scala.concurrent._
import scala.concurrent.duration._

object AskExample extends App {
  implicit val system: ActorSystem[EchoActor.Message] =
    ActorSystem(EchoActor(), "ask-example")
  import system.executionContext
  implicit val askTimeout: Timeout = 3.seconds

  // EchoActor
  val actorRef: ActorRef[EchoActor.Message] = system

  // ? または ask でアクターへ問い合わせを行う
  // 返信は Future[T] として受け取ることができる
  val responseFuture1: Future[String] =
    actorRef ? (replyTo => EchoActor.Message("test1", replyTo))
  val responseFuture2: Future[String] =
    actorRef.ask(replyTo => EchoActor.Message("test2", replyTo))

  // 結果をコンソールに表示してみる
  responseFuture1.foreach { response1: String =>
    println(response1)
  }
  responseFuture2.foreach { response2: String =>
    println(response2)
  }

  // Future が完了するのを3秒待ち、ActorSystemを終了させる
  Await.ready(responseFuture1, 3.seconds)
  Await.ready(responseFuture2, 3.seconds)
  system.terminate()
}
