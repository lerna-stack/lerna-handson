package example.model.concert.actor

import akka.actor._
import akka.testkit.TestKit
import example.ActorSpecBase
import example.model.concert.ConcertError._

/** ConcertActor の 共通テスト を定義する
  *
  * テスト共通化のため、このような形式でテストケースを実装する。
  *
  * テスト共通化の方法については、
  * [[https://www.scalatest.org/user_guide/sharing_tests Sharing tests]]
  * を参照すること
  */
trait ConcertActorBehaviors {
  this: ActorSpecBase =>

  import example.model.concert._
  import example.model.concert.actor.ConcertActorProtocol._

  // TODO Use typed testKit
  val classicTestKit = new TestKit(system.classicSystem)
  import classicTestKit._
  implicit def self: ActorRef = classicTestKit.testActor

  class EmptyConcertActorFactory(props: Props) {
    def create(id: ConcertId): ActorRef = {
      system.classicSystem.actorOf(props, ConcertActorBase.actorNameFor(id))
    }
  }

  class AvailableConcertActorFactory(props: Props) {
    private val underlyingFactory = new EmptyConcertActorFactory(props)
    def create(id: ConcertId, numOfTickets: Int): ActorRef = {
      val actor = underlyingFactory.create(id)
      actor ! CreateConcertRequest(id, numOfTickets)
      expectMsgType[CreateConcertSucceeded]
      actor
    }
  }

  class CancelledConcertActorFactory(props: Props) {
    private val underlyingFactory = new AvailableConcertActorFactory(props)
    def create(id: ConcertId, numOfTickets: Int): ActorRef = {
      val actor = underlyingFactory.create(id, numOfTickets)
      actor ! CancelConcertRequest(id)
      expectMsgType[CancelConcertSucceeded]
      actor
    }
  }

  val idGenerator         = new ConcertIdGenerator()
  def nextId(): ConcertId = idGenerator.nextId()

  def emptyConcertActor(newConcertActor: EmptyConcertActorFactory): Unit = {

    "can create a concert" in {
      val id           = nextId()
      val actor        = newConcertActor.create(id)
      val numOfTickets = 3

      actor ! CreateConcertRequest(id, numOfTickets)
      val resp = expectMsgType[CreateConcertSucceeded]
      resp.numTickets shouldBe numOfTickets
    }

    "cannot get the concert if it is not created yet" in {
      val id    = nextId()
      val actor = newConcertActor.create(id)

      actor ! GetConcertRequest(id)
      val resp = expectMsgType[GetConcertFailed]
      resp.error shouldBe a[ConcertNotFoundError]
    }

    "cannot cancel a concert if the concert is not created" in {
      val id    = nextId()
      val actor = newConcertActor.create(id)

      actor ! CancelConcertRequest(id)
      val resp = expectMsgType[CancelConcertFailed]
      resp.error shouldBe a[ConcertNotFoundError]
    }

  }

  def availableConcertActor(newConcertActor: AvailableConcertActorFactory): Unit = {

    "cannot create a concert if it's already exists" in {
      val id    = nextId()
      val actor = newConcertActor.create(id, numOfTickets = 3)

      actor ! CreateConcertRequest(id, 2)
      val resp = expectMsgType[CreateConcertFailed]
      resp.error shouldBe a[DuplicatedConcertError]
    }

    "can get the concert" in {
      val id    = nextId()
      val actor = newConcertActor.create(id, numOfTickets = 3)

      actor ! GetConcertRequest(id)
      val resp = expectMsgType[GetConcertSucceeded]
      resp.id shouldBe id
      resp.tickets.size shouldBe 3
    }

    "can cancel a concert" in {
      val id    = nextId()
      val actor = newConcertActor.create(id, numOfTickets = 3)

      actor ! CancelConcertRequest(id)
      val resp = expectMsgType[CancelConcertSucceeded]
      resp.numberOfTickets shouldBe 3
    }

    "can buy tickets" in {
      val id    = nextId()
      val actor = newConcertActor.create(id, numOfTickets = 2)

      actor ! BuyConcertTicketsRequest(id, 2)
      val resp = expectMsgType[BuyConcertTicketsSucceeded]
      resp.tickets.size shouldBe 2
    }

    "cannot buy no tickets" in {
      val id    = nextId()
      val actor = newConcertActor.create(id, numOfTickets = 2)

      actor ! BuyConcertTicketsRequest(id, 0)
      val resp = expectMsgType[BuyConcertTicketsFailed]
      resp.error shouldBe a[InvalidConcertOperationError]
    }

    "cannot buy exceeded tickets" in {
      val id    = nextId()
      val actor = newConcertActor.create(id, numOfTickets = 2)

      actor ! BuyConcertTicketsRequest(id, 3)
      val resp = expectMsgType[BuyConcertTicketsFailed]
      resp.error shouldBe a[InvalidConcertOperationError]
    }

  }

  def cancelledConcertActor(newConcertActor: CancelledConcertActorFactory): Unit = {

    "can get the concert even if it is cancelled" in {
      val id    = nextId()
      val actor = newConcertActor.create(id, numOfTickets = 2)
      actor ! GetConcertRequest(id)
      val resp = expectMsgType[GetConcertSucceeded]
      resp.tickets.size shouldBe 2
      resp.cancelled shouldBe true
    }

    "cannot cancel a concert if the concert is already cancelled" in {
      val id    = nextId()
      val actor = newConcertActor.create(id, numOfTickets = 1)

      actor ! CancelConcertRequest(id)
      val resp = expectMsgType[CancelConcertFailed]
      resp.error shouldBe a[InvalidConcertOperationError]
    }

  }

}
