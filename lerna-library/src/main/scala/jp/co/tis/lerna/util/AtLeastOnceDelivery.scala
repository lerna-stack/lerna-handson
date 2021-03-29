package jp.co.tis.lerna.util

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Cancellable, Props }
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._

sealed trait AtLeastOnceDeliveryCommand

object AtLeastOnceDelivery {
  def askTo(
      destination: ActorRef,
      message: Any,
  )(implicit system: ActorSystem, timeout: Timeout): Future[Any] = {
    val atLeastOnceDeliveryProxy = system.actorOf(AtLeastOnceDelivery.props(destination))
    atLeastOnceDeliveryProxy ? message
  }

  def tellTo(
      destination: ActorRef,
      message: Any,
  )(implicit system: ActorSystem, sender: ActorRef = Actor.noSender): Unit = {
    val atLeastOnceDeliveryProxy = system.actorOf(AtLeastOnceDelivery.props(destination))
    atLeastOnceDeliveryProxy ! message
  }

  final case class AtLeastOnceDeliveryRequest(originalMessage: Any)(implicit self: ActorRef)
      extends AtLeastOnceDeliveryCommand {

    /** メッセージの到着を通知する<br>
      *   accept しない限り同じメッセージが届き続ける
      */
    def accept(): Unit = self ! AtLeastOnceDeliveryConfirm
  }

  private case object AtLeastOnceDeliveryConfirm extends AtLeastOnceDeliveryCommand

  private case object SendRequest      extends AtLeastOnceDeliveryCommand
  private case object RetrySendRequest extends AtLeastOnceDeliveryCommand
  private case object RetryTimeout     extends AtLeastOnceDeliveryCommand

  private def props(destination: ActorRef) =
    Props(new AtLeastOnceDelivery(destination))
}

/** 到達保証用のActor<br>
  * 1 リクエスト -> 1 Actor<br>
  * ※ 再度Actorが作成されるケースは考えていない<br>
  */
class AtLeastOnceDelivery(destination: ActorRef) extends Actor with ActorLogging {
  import AtLeastOnceDelivery._

  private val config =
    context.system.settings.config.getConfig("jp.co.tis.lerna.util.at-least-once-delivery")

  private val redeliverInterval: FiniteDuration = Duration.fromNanos(config.getDuration("redeliver-interval").toNanos)
  private val retryTimeout: FiniteDuration      = Duration.fromNanos(config.getDuration("retry-timeout").toNanos)

  import context.dispatcher
  context.system.scheduler.scheduleOnce(delay = retryTimeout, receiver = self, message = RetryTimeout)

  import context.dispatcher

  override def receive: Receive = {
    case RetryTimeout =>
      context.stop(self)

    case message =>
      val replyTo = sender()
      val retryScheduler =
        context.system.scheduler.scheduleAtFixedRate(
          initialDelay = redeliverInterval,
          redeliverInterval,
          self,
          RetrySendRequest,
        )
      context.become(accepted(replyTo, message, retryScheduler))
      self ! SendRequest
  }

  def accepted(replyTo: ActorRef, message: Any, retryScheduler: Cancellable): Receive = {
    case RetrySendRequest =>
      log.info(s"再送します: destination = $destination")
      self ! SendRequest

    case SendRequest =>
      destination.tell(AtLeastOnceDeliveryRequest(message), replyTo.actorRef)

    case AtLeastOnceDeliveryConfirm =>
      context.stop(self)
      retryScheduler.cancel()

    case RetryTimeout =>
      context.stop(self)
      retryScheduler.cancel()
      log.info(s"到達確認ができず、${retryTimeout} 経過したため再送を中止します")
  }
}
