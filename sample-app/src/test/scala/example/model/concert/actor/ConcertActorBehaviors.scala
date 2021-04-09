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
  import example.model.concert.actor.ConcertActorProtocol._

  class EmptyConcertActorFactory(createBehavior: ConcertActorBehaviorFactory) {
    def create(id: ConcertId): ActorRef[ConcertCommandRequest] = {
      testKit.spawn(createBehavior(id, PersistenceId.ofUniqueId(id.value)))
    }
  }

  class AvailableConcertActorFactory(createBehavior: ConcertActorBehaviorFactory) {
    private val underlyingFactory = new EmptyConcertActorFactory(createBehavior)
    def create(id: ConcertId, numOfTickets: Int): ActorRef[ConcertCommandRequest] = {
      val actor = underlyingFactory.create(id)
      val probe = testKit.createTestProbe[CreateConcertResponse]()
      actor ! CreateConcertRequest(numOfTickets, probe.ref)
      probe.expectMessageType[CreateConcertSucceeded]
      actor
    }
  }

  class CancelledConcertActorFactory(createBehavior: ConcertActorBehaviorFactory) {
    private val underlyingFactory = new AvailableConcertActorFactory(createBehavior)
    def create(id: ConcertId, numOfTickets: Int): ActorRef[ConcertCommandRequest] = {
      val actor = underlyingFactory.create(id, numOfTickets)
      val probe = testKit.createTestProbe[CancelConcertResponse]()
      actor ! CancelConcertRequest(probe.ref)
      probe.expectMessageType[CancelConcertSucceeded]
      actor
    }
  }

  def emptyConcertActor(newConcertActor: EmptyConcertActorFactory): Unit = {

    "can create a concert" in {
      val id           = newConcertId()
      val actor        = newConcertActor.create(id)
      val numOfTickets = 3

      val probe = testKit.createTestProbe[CreateConcertResponse]()
      actor ! CreateConcertRequest(numOfTickets, probe.ref)
      val resp = probe.expectMessageType[CreateConcertSucceeded]
      resp.numTickets shouldBe numOfTickets
    }

    "cannot get the concert if it is not created yet" in {
      val id    = newConcertId()
      val actor = newConcertActor.create(id)

      val probe = testKit.createTestProbe[GetConcertResponse]()
      actor ! GetConcertRequest(probe.ref)
      val resp = probe.expectMessageType[GetConcertFailed]
      resp.error shouldBe a[ConcertNotFoundError]
    }

    "cannot cancel a concert if the concert is not created" in {
      val id    = newConcertId()
      val actor = newConcertActor.create(id)

      val probe = testKit.createTestProbe[CancelConcertResponse]()
      actor ! CancelConcertRequest(probe.ref)
      val resp = probe.expectMessageType[CancelConcertFailed]
      resp.error shouldBe a[ConcertNotFoundError]
    }

  }

  def availableConcertActor(newConcertActor: AvailableConcertActorFactory): Unit = {

    "cannot create a concert if it's already exists" in {
      val id    = newConcertId()
      val actor = newConcertActor.create(id, numOfTickets = 3)

      val probe = testKit.createTestProbe[CreateConcertResponse]()
      actor ! CreateConcertRequest(2, probe.ref)
      val resp = probe.expectMessageType[CreateConcertFailed]
      resp.error shouldBe a[DuplicatedConcertError]
    }

    "can get the concert" in {
      val id    = newConcertId()
      val actor = newConcertActor.create(id, numOfTickets = 3)

      val probe = testKit.createTestProbe[GetConcertResponse]()
      actor ! GetConcertRequest(probe.ref)
      val resp = probe.expectMessageType[GetConcertSucceeded]
      resp.tickets.size shouldBe 3
    }

    "can cancel a concert" in {
      val id    = newConcertId()
      val actor = newConcertActor.create(id, numOfTickets = 3)

      val probe = testKit.createTestProbe[CancelConcertResponse]()
      actor ! CancelConcertRequest(probe.ref)
      val resp = probe.expectMessageType[CancelConcertSucceeded]
      resp.numberOfTickets shouldBe 3
    }

    "can buy tickets" in {
      val id    = newConcertId()
      val actor = newConcertActor.create(id, numOfTickets = 2)

      val probe = testKit.createTestProbe[BuyConcertTicketsResponse]()
      actor ! BuyConcertTicketsRequest(2, probe.ref)
      val resp = probe.expectMessageType[BuyConcertTicketsSucceeded]
      resp.tickets.size shouldBe 2
    }

    "cannot buy no tickets" in {
      val id    = newConcertId()
      val actor = newConcertActor.create(id, numOfTickets = 2)

      val probe = testKit.createTestProbe[BuyConcertTicketsResponse]()
      actor ! BuyConcertTicketsRequest(0, probe.ref)
      val resp = probe.expectMessageType[BuyConcertTicketsFailed]
      resp.error shouldBe a[InvalidConcertOperationError]
    }

    "cannot buy exceeded tickets" in {
      val id    = newConcertId()
      val actor = newConcertActor.create(id, numOfTickets = 2)

      val probe = testKit.createTestProbe[BuyConcertTicketsResponse]()
      actor ! BuyConcertTicketsRequest(3, probe.ref)
      val resp = probe.expectMessageType[BuyConcertTicketsFailed]
      resp.error shouldBe a[InvalidConcertOperationError]
    }

  }

  def cancelledConcertActor(newConcertActor: CancelledConcertActorFactory): Unit = {

    "can get the concert even if it is cancelled" in {
      val id    = newConcertId()
      val actor = newConcertActor.create(id, numOfTickets = 2)

      val probe = testKit.createTestProbe[GetConcertResponse]()
      actor ! GetConcertRequest(probe.ref)
      val resp = probe.expectMessageType[GetConcertSucceeded]
      resp.tickets.size shouldBe 2
      resp.cancelled shouldBe true
    }

    "cannot cancel a concert if the concert is already cancelled" in {
      val id    = newConcertId()
      val actor = newConcertActor.create(id, numOfTickets = 1)

      val probe = testKit.createTestProbe[CancelConcertResponse]()
      actor ! CancelConcertRequest(probe.ref)
      val resp = probe.expectMessageType[CancelConcertFailed]
      resp.error shouldBe a[InvalidConcertOperationError]
    }

  }

}
