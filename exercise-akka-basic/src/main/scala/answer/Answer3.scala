package answer

import akka.actor._

final class DefaultCounterActor extends Actor {
  private var counter: Int = 0
  override def receive: Receive = {
    case delta: Int =>
      counter += delta
      sender() ! counter
    case _ =>
      counter = 0
      sender() ! counter
  }
}
