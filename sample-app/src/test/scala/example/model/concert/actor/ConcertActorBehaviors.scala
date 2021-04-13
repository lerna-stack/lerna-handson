package example.model.concert.actor

import akka.actor.typed.ActorRef
import akka.persistence.typed.PersistenceId
import example.ActorSpecBase
import example.model.concert.ConcertError._
import example.model.concert.ConcertIdGeneratorSupport

/** ConcertActor の 共通テスト を定義する
  *
  * テスト共通化のため、このような形式でテストケースを実装する。
  *
  * テスト共通化の方法については、
  * [[https://www.scalatest.org/user_guide/sharing_tests Sharing tests]]
  * を参照すること
  */
trait ConcertActorBehaviors extends ConcertIdGeneratorSupport {
  this: ActorSpecBase =>

  import example.model.concert._
  import example.model.concert.actor.ConcertActor._

  private class EmptyConcertActorFactory(createBehavior: ConcertActorBehaviorFactory) {
    def create(id: ConcertId): ActorRef[Command] = {
      testKit.spawn(createBehavior(id, PersistenceId.ofUniqueId(id.value)))
    }
  }

  private class AvailableConcertActorFactory(createBehavior: ConcertActorBehaviorFactory) {
    private val underlyingFactory = new EmptyConcertActorFactory(createBehavior)
    def create(id: ConcertId, numOfTickets: Int): ActorRef[Command] = {
      val actor = underlyingFactory.create(id)
      val probe = testKit.createTestProbe[CreateResponse]()
      actor ! Create(numOfTickets, probe.ref)
      probe.expectMessageType[CreateSucceeded]
      actor
    }
  }

  private class CancelledConcertActorFactory(createBehavior: ConcertActorBehaviorFactory) {
    private val underlyingFactory = new AvailableConcertActorFactory(createBehavior)
    def create(id: ConcertId, numOfTickets: Int): ActorRef[Command] = {
      val actor = underlyingFactory.create(id, numOfTickets)
      val probe = testKit.createTestProbe[CancelResponse]()
      actor ! Cancel(probe.ref)
      probe.expectMessageType[CancelSucceeded]
      actor
    }
  }

  def emptyConcertActor(createBehavior: ConcertActorBehaviorFactory): Unit = {

    val factory = new EmptyConcertActorFactory(createBehavior)

    "can create a concert" in {
      val id           = newConcertId()
      val actor        = factory.create(id)
      val numOfTickets = 3

      val probe = testKit.createTestProbe[CreateResponse]()
      actor ! Create(numOfTickets, probe.ref)
      val resp = probe.expectMessageType[CreateSucceeded]
      resp.numTickets shouldBe numOfTickets
    }

    "cannot get the concert if it is not created yet" in {
      val id    = newConcertId()
      val actor = factory.create(id)

      val probe = testKit.createTestProbe[GetResponse]()
      actor ! Get(probe.ref)
      val resp = probe.expectMessageType[GetFailed]
      resp.error shouldBe a[ConcertNotFoundError]
    }

    "cannot cancel a concert if the concert is not created" in {
      val id    = newConcertId()
      val actor = factory.create(id)

      val probe = testKit.createTestProbe[CancelResponse]()
      actor ! Cancel(probe.ref)
      val resp = probe.expectMessageType[CancelFailed]
      resp.error shouldBe a[ConcertNotFoundError]
    }

  }

  def availableConcertActor(createBehavior: ConcertActorBehaviorFactory): Unit = {

    val factory = new AvailableConcertActorFactory(createBehavior)

    "cannot create a concert if it's already exists" in {
      val id    = newConcertId()
      val actor = factory.create(id, numOfTickets = 3)

      val probe = testKit.createTestProbe[CreateResponse]()
      actor ! Create(2, probe.ref)
      val resp = probe.expectMessageType[CreateFailed]
      resp.error shouldBe a[DuplicatedConcertError]
    }

    "can get the concert" in {
      val id    = newConcertId()
      val actor = factory.create(id, numOfTickets = 3)

      val probe = testKit.createTestProbe[GetResponse]()
      actor ! Get(probe.ref)
      val resp = probe.expectMessageType[GetSucceeded]
      resp.tickets.size shouldBe 3
    }

    "can cancel a concert" in {
      val id    = newConcertId()
      val actor = factory.create(id, numOfTickets = 3)

      val probe = testKit.createTestProbe[CancelResponse]()
      actor ! Cancel(probe.ref)
      val resp = probe.expectMessageType[CancelSucceeded]
      resp.numberOfTickets shouldBe 3
    }

    "can buy tickets" in {
      val id    = newConcertId()
      val actor = factory.create(id, numOfTickets = 2)

      val probe = testKit.createTestProbe[BuyTicketsResponse]()
      actor ! BuyTickets(2, probe.ref)
      val resp = probe.expectMessageType[BuyTicketsSucceeded]
      resp.tickets.size shouldBe 2
    }

    "cannot buy no tickets" in {
      val id    = newConcertId()
      val actor = factory.create(id, numOfTickets = 2)

      val probe = testKit.createTestProbe[BuyTicketsResponse]()
      actor ! BuyTickets(0, probe.ref)
      val resp = probe.expectMessageType[BuyTicketsFailed]
      resp.error shouldBe a[InvalidConcertOperationError]
    }

    "cannot buy exceeded tickets" in {
      val id    = newConcertId()
      val actor = factory.create(id, numOfTickets = 2)

      val probe = testKit.createTestProbe[BuyTicketsResponse]()
      actor ! BuyTickets(3, probe.ref)
      val resp = probe.expectMessageType[BuyTicketsFailed]
      resp.error shouldBe a[InvalidConcertOperationError]
    }

  }

  def cancelledConcertActor(createBehavior: ConcertActorBehaviorFactory): Unit = {

    val factory = new CancelledConcertActorFactory(createBehavior)

    "can get the concert even if it is cancelled" in {
      val id    = newConcertId()
      val actor = factory.create(id, numOfTickets = 2)

      val probe = testKit.createTestProbe[GetResponse]()
      actor ! Get(probe.ref)
      val resp = probe.expectMessageType[GetSucceeded]
      resp.tickets.size shouldBe 2
      resp.cancelled shouldBe true
    }

    "cannot cancel a concert if the concert is already cancelled" in {
      val id    = newConcertId()
      val actor = factory.create(id, numOfTickets = 1)

      val probe = testKit.createTestProbe[CancelResponse]()
      actor ! Cancel(probe.ref)
      val resp = probe.expectMessageType[CancelFailed]
      resp.error shouldBe a[InvalidConcertOperationError]
    }

  }

}
