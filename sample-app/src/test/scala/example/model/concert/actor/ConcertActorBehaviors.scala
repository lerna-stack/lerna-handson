package example.model.concert.actor

import akka.actor.typed.ActorRef
import akka.persistence.testkit.scaladsl.{ PersistenceTestKit, SnapshotTestKit }
import akka.persistence.typed.PersistenceId
import example.ActorSpecBase
import example.model.concert.ConcertIdGeneratorSupport
import org.scalatest.BeforeAndAfterEach

/** ConcertActor の 共通テスト を定義する
  *
  * テスト共通化のため、このような形式でテストケースを実装する。
  *
  * テスト共通化の方法については、
  * [[https://www.scalatest.org/user_guide/sharing_tests Sharing tests]]
  * を参照すること
  */
trait ConcertActorBehaviors extends BeforeAndAfterEach with ConcertIdGeneratorSupport {
  this: ActorSpecBase =>

  import example.model.concert.ConcertError._
  import example.model.concert.ConcertEvent._
  import example.model.concert._
  import example.model.concert.actor.ConcertActor._

  private val persistenceTestKit = PersistenceTestKit(system)
  private val snapshotTestKit    = SnapshotTestKit(system)

  override def beforeEach(): Unit = {
    super.beforeEach()
    persistenceTestKit.clearAll()
    snapshotTestKit.clearAll()
  }

  private class EmptyConcertActorFactory(createBehavior: ConcertActorBehaviorFactory) {
    def create(id: ConcertId, persistenceId: PersistenceId): ActorRef[Command] = {
      testKit.spawn(createBehavior(id, persistenceId))
    }
  }

  private class AvailableConcertActorFactory(createBehavior: ConcertActorBehaviorFactory) {
    private val underlyingFactory = new EmptyConcertActorFactory(createBehavior)
    def create(id: ConcertId, persistenceId: PersistenceId, numOfTickets: Int): ActorRef[Command] = {
      val actor = underlyingFactory.create(id, persistenceId)
      val probe = testKit.createTestProbe[CreateResponse]()
      actor ! Create(numOfTickets, probe.ref)
      probe.expectMessageType[CreateSucceeded]
      persistenceTestKit.expectNextPersistedType[ConcertCreated](persistenceId.id)
      actor
    }
  }

  private class CancelledConcertActorFactory(createBehavior: ConcertActorBehaviorFactory) {
    private val underlyingFactory = new AvailableConcertActorFactory(createBehavior)
    def create(id: ConcertId, persistenceId: PersistenceId, numOfTickets: Int): ActorRef[Command] = {
      val actor = underlyingFactory.create(id, persistenceId, numOfTickets)
      val probe = testKit.createTestProbe[CancelResponse]()
      actor ! Cancel(probe.ref)
      probe.expectMessageType[CancelSucceeded]
      persistenceTestKit.expectNextPersistedType[ConcertCancelled](persistenceId.id)
      actor
    }
  }

  def emptyConcertActor(createBehavior: ConcertActorBehaviorFactory): Unit = {

    val factory = new EmptyConcertActorFactory(createBehavior)

    "can create a concert" in {
      val id            = newConcertId()
      val persistenceId = PersistenceId.ofUniqueId(id.value)
      val actor         = factory.create(id, persistenceId)
      val numOfTickets  = 3

      val probe = testKit.createTestProbe[CreateResponse]()
      actor ! Create(numOfTickets, probe.ref)
      val resp = probe.expectMessageType[CreateSucceeded]
      resp.numTickets shouldBe numOfTickets
      val createdEvent = persistenceTestKit.expectNextPersistedType[ConcertCreated](persistenceId.id)
      createdEvent.concertId shouldBe id
      createdEvent.numOfTickets shouldBe numOfTickets
    }

    "cannot get the concert if it is not created yet" in {
      val id            = newConcertId()
      val persistenceId = PersistenceId.ofUniqueId(id.value)
      val actor         = factory.create(id, persistenceId)

      val probe = testKit.createTestProbe[GetResponse]()
      actor ! Get(probe.ref)
      val resp = probe.expectMessageType[GetFailed]
      resp.error shouldBe a[ConcertNotFoundError]
      persistenceTestKit.expectNothingPersisted(persistenceId.id)
    }

    "cannot cancel a concert if the concert is not created" in {
      val id            = newConcertId()
      val persistenceId = PersistenceId.ofUniqueId(id.value)
      val actor         = factory.create(id, persistenceId)

      val probe = testKit.createTestProbe[CancelResponse]()
      actor ! Cancel(probe.ref)
      val resp = probe.expectMessageType[CancelFailed]
      resp.error shouldBe a[ConcertNotFoundError]
      persistenceTestKit.expectNothingPersisted(persistenceId.id)
    }

  }

  def availableConcertActor(createBehavior: ConcertActorBehaviorFactory): Unit = {

    val factory = new AvailableConcertActorFactory(createBehavior)

    "cannot create a concert if it's already exists" in {
      val id            = newConcertId()
      val persistenceId = PersistenceId.ofUniqueId(id.value)
      val actor         = factory.create(id, persistenceId, numOfTickets = 3)

      val probe = testKit.createTestProbe[CreateResponse]()
      actor ! Create(2, probe.ref)
      val resp = probe.expectMessageType[CreateFailed]
      resp.error shouldBe a[DuplicatedConcertError]
      persistenceTestKit.expectNothingPersisted(persistenceId.id)
    }

    "can get the concert" in {
      val id            = newConcertId()
      val persistenceId = PersistenceId.ofUniqueId(id.value)
      val actor         = factory.create(id, persistenceId, numOfTickets = 3)

      val probe = testKit.createTestProbe[GetResponse]()
      actor ! Get(probe.ref)
      val resp = probe.expectMessageType[GetSucceeded]
      resp.tickets.size shouldBe 3
      persistenceTestKit.expectNothingPersisted(persistenceId.id)
    }

    "can cancel a concert" in {
      val id            = newConcertId()
      val persistenceId = PersistenceId.ofUniqueId(id.value)
      val actor         = factory.create(id, persistenceId, numOfTickets = 3)

      val probe = testKit.createTestProbe[CancelResponse]()
      actor ! Cancel(probe.ref)
      val resp = probe.expectMessageType[CancelSucceeded]
      resp.numberOfTickets shouldBe 3
      val cancelledEvent = persistenceTestKit.expectNextPersistedType[ConcertCancelled](persistenceId.id)
      cancelledEvent.concertId shouldBe id
    }

    "can buy tickets" in {
      val id            = newConcertId()
      val persistenceId = PersistenceId.ofUniqueId(id.value)
      val actor         = factory.create(id, persistenceId, numOfTickets = 2)

      val probe = testKit.createTestProbe[BuyTicketsResponse]()
      actor ! BuyTickets(2, probe.ref)
      val resp = probe.expectMessageType[BuyTicketsSucceeded]
      resp.tickets.size shouldBe 2
      val boughtEvent = persistenceTestKit.expectNextPersistedType[ConcertTicketsBought](persistenceId.id)
      boughtEvent.concertId shouldBe id
      boughtEvent.tickets shouldBe resp.tickets
    }

    "cannot buy no tickets" in {
      val id            = newConcertId()
      val persistenceId = PersistenceId.ofUniqueId(id.value)
      val actor         = factory.create(id, persistenceId, numOfTickets = 2)

      val probe = testKit.createTestProbe[BuyTicketsResponse]()
      actor ! BuyTickets(0, probe.ref)
      val resp = probe.expectMessageType[BuyTicketsFailed]
      resp.error shouldBe a[InvalidConcertOperationError]
      persistenceTestKit.expectNothingPersisted(persistenceId.id)
    }

    "cannot buy exceeded tickets" in {
      val id            = newConcertId()
      val persistenceId = PersistenceId.ofUniqueId(id.value)
      val actor         = factory.create(id, persistenceId, numOfTickets = 2)

      val probe = testKit.createTestProbe[BuyTicketsResponse]()
      actor ! BuyTickets(3, probe.ref)
      val resp = probe.expectMessageType[BuyTicketsFailed]
      resp.error shouldBe a[InvalidConcertOperationError]
      persistenceTestKit.expectNothingPersisted(persistenceId.id)
    }

  }

  def cancelledConcertActor(createBehavior: ConcertActorBehaviorFactory): Unit = {

    val factory = new CancelledConcertActorFactory(createBehavior)

    "can get the concert even if it is cancelled" in {
      val id            = newConcertId()
      val persistenceId = PersistenceId.ofUniqueId(id.value)
      val actor         = factory.create(id, persistenceId, numOfTickets = 2)

      val probe = testKit.createTestProbe[GetResponse]()
      actor ! Get(probe.ref)
      val resp = probe.expectMessageType[GetSucceeded]
      resp.tickets.size shouldBe 2
      resp.cancelled shouldBe true
      persistenceTestKit.expectNothingPersisted(persistenceId.id)
    }

    "cannot cancel a concert if the concert is already cancelled" in {
      val id            = newConcertId()
      val persistenceId = PersistenceId.ofUniqueId(id.value)
      val actor         = factory.create(id, persistenceId, numOfTickets = 1)

      val probe = testKit.createTestProbe[CancelResponse]()
      actor ! Cancel(probe.ref)
      val resp = probe.expectMessageType[CancelFailed]
      resp.error shouldBe a[InvalidConcertOperationError]
      persistenceTestKit.expectNothingPersisted(persistenceId.id)
    }

  }

  // 演習実施順序の都合により、 Snapshot を保存しない実装パターンがある。
  // このため、イベント永続化のテストとは異なり、個別でテストケースを記述する。
  // 製品開発等では、イベント永続化と同じようにアクターのテストと同時にテストしたほうが良い。
  def snapshotPersistenceActor(createBehavior: ConcertActorBehaviorFactory): Unit = {
    val id            = newConcertId()
    val persistenceId = PersistenceId.ofUniqueId(id.value)
    val actor         = testKit.spawn(createBehavior(id, persistenceId))
    val probe         = testKit.createTestProbe[Response]()

    actor ! Create(numTickets = 2, probe.ref)
    probe.expectMessageType[CreateSucceeded]
    snapshotTestKit.expectNothingPersisted(persistenceId.id)

    actor ! BuyTickets(1, probe.ref)
    probe.expectMessageType[BuyTicketsSucceeded]
    snapshotTestKit.expectNothingPersisted(persistenceId.id)

    actor ! Cancel(probe.ref)
    probe.expectMessageType[CancelSucceeded]
    // スナップショットが取得されることをテストする。
    // コードを簡潔にするため、スナップショットの内容はチェックしない。
    // 製品開発等では、スナップショットの内容をテストしたほうが良い(`Any`ではなく具体的な State の型を指定する)。
    snapshotTestKit.expectNextPersistedType[Any](persistenceId.id)

  }

}
