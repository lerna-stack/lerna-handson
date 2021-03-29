package jp.co.tis.lerna.util

import java.util.concurrent.atomic.AtomicInteger

import scala.concurrent.duration._
import akka.actor.{ ActorSystem, NoSerializationVerificationNeeded }
import akka.testkit.{ ImplicitSender, TestKit, TestProbe }
import akka.util.Timeout
import com.typesafe.config.{ Config, ConfigFactory }
import jp.co.tis.lerna.testkit.StandardSpec
import jp.co.tis.lerna.util.AtLeastOnceDelivery.AtLeastOnceDeliveryRequest
import org.scalatest.Inside
import org.scalatest.concurrent.ScalaFutures

object AtLeastOnceDeliverySpec {
  private val redeliverInterval = 100.milliseconds
  private val retryTimeout      = 1000.milliseconds

  private val config: Config = ConfigFactory
    .parseString(s"""
                    | akka.actor {
                    |   provider = local
                    | }
                    | jp.co.tis.lerna.util.at-least-once-delivery {
                    |   redeliver-interval = ${redeliverInterval.toMillis} ms
                    |   retry-timeout = ${retryTimeout.toMillis} ms
                    | }
       """.stripMargin)
    .withFallback(ConfigFactory.load())

  final case class RequestMessage(message: String)  extends NoSerializationVerificationNeeded
  final case class ResponseMessage(message: String) extends NoSerializationVerificationNeeded
}

class AtLeastOnceDeliverySpec
    extends TestKit(ActorSystem("AtLeastOnceDeliverySpec", AtLeastOnceDeliverySpec.config))
    with ImplicitSender
    with StandardSpec
    with ScalaFutures
    with Inside {

  import AtLeastOnceDeliverySpec._

  implicit val askTimeout: Timeout = 5.seconds

  "askTo()" should {
    "宛先に到達保証用メッセージを送信できる" in {
      val destinationProbe = TestProbe()

      val requestMessage  = RequestMessage(s"request-${generateUniqueNumber()}")
      val responseMessage = ResponseMessage(s"response-${generateUniqueNumber()}")

      val resultFuture = AtLeastOnceDelivery.askTo(destinationProbe.ref, requestMessage).mapTo[ResponseMessage]

      {
        // destination側
        val request = destinationProbe.expectMsgType[AtLeastOnceDeliveryRequest]
        expect(request.originalMessage === requestMessage)

        request.accept()
        destinationProbe.reply(responseMessage)
      }

      whenReady(resultFuture) { result =>
        expect(result === responseMessage)
      }
    }

    "accept()されない場合は再送する" in {
      val destinationProbe = TestProbe()
      val requestMessage   = RequestMessage(s"request-${generateUniqueNumber()}")

      AtLeastOnceDelivery.askTo(destinationProbe.ref, requestMessage)

      {
        // destination側
        val request1 = destinationProbe.expectMsgType[AtLeastOnceDeliveryRequest]
        expect(request1.originalMessage === requestMessage)

        val request2 = destinationProbe.expectMsgType[AtLeastOnceDeliveryRequest]
        expect(request2.originalMessage === requestMessage)

        val request3 = destinationProbe.expectMsgType[AtLeastOnceDeliveryRequest]
        expect(request3.originalMessage === requestMessage)

        val request4 = destinationProbe.expectMsgType[AtLeastOnceDeliveryRequest]
        expect(request4.originalMessage === requestMessage)

        request4.accept()
        destinationProbe.expectNoMessage()
      }
    }

    "retry-timeout時間経過しても accept()されなかった場合再送を中止する" in {
      val destinationProbe = TestProbe()

      val requestMessage = RequestMessage(s"request-${generateUniqueNumber()}")

      AtLeastOnceDelivery.askTo(destinationProbe.ref, requestMessage)

      {
        // destination側
        destinationProbe.receiveWhile(max = retryTimeout * 1.1) {
          case request: AtLeastOnceDeliveryRequest =>
            expect(request.originalMessage === requestMessage)
        }

        destinationProbe.expectNoMessage()
      }
    }
  }

  "tellTo()" should {
    "宛先に到達保証用メッセージを送信できる" in {
      val destinationProbe = TestProbe()

      val requestMessage  = RequestMessage(s"request-${generateUniqueNumber()}")
      val responseMessage = ResponseMessage(s"response-${generateUniqueNumber()}")

      AtLeastOnceDelivery.tellTo(destinationProbe.ref, requestMessage)

      {
        // destination側
        val request = destinationProbe.expectMsgType[AtLeastOnceDeliveryRequest]
        expect(request.originalMessage === requestMessage)

        request.accept()
        destinationProbe.reply(responseMessage)
      }

      expectMsg(responseMessage)
    }

    "accept()されない場合は再送する" in {
      val destinationProbe = TestProbe()

      val requestMessage = RequestMessage(s"request-${generateUniqueNumber()}")

      AtLeastOnceDelivery.tellTo(destinationProbe.ref, requestMessage)

      {
        // destination側
        val request1 = destinationProbe.expectMsgType[AtLeastOnceDeliveryRequest]
        expect(request1.originalMessage === requestMessage)

        val request2 = destinationProbe.expectMsgType[AtLeastOnceDeliveryRequest]
        expect(request2.originalMessage === requestMessage)

        val request3 = destinationProbe.expectMsgType[AtLeastOnceDeliveryRequest]
        expect(request3.originalMessage === requestMessage)

        val request4 = destinationProbe.expectMsgType[AtLeastOnceDeliveryRequest]
        expect(request4.originalMessage === requestMessage)

        request4.accept()
        destinationProbe.expectNoMessage()
      }
    }

    "retry-timeout時間経過しても accept()されなかった場合再送を中止する" in {
      val destinationProbe = TestProbe()

      val requestMessage = RequestMessage(s"request-${generateUniqueNumber()}")

      AtLeastOnceDelivery.askTo(destinationProbe.ref, requestMessage)

      {
        // destination側
        destinationProbe.receiveWhile(max = retryTimeout * 1.1) {
          case request: AtLeastOnceDeliveryRequest =>
            expect(request.originalMessage === requestMessage)
        }

        destinationProbe.expectNoMessage()
      }
      shutdown()
    }
  }

  private val generateUniqueNumber: () => Int = {
    val counter = new AtomicInteger()
    () => counter.getAndIncrement()
  }
}
