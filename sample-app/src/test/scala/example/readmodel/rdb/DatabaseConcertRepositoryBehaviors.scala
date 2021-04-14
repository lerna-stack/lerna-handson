package example.readmodel.rdb

import akka.persistence.query.TimeBasedUUID
import example.model.concert.ConcertEvent.{ ConcertCancelled, ConcertCreated, ConcertTicketsBought }
import example.model.concert.{ ConcertId, ConcertIdGenerator, ConcertTicketId }
import example.readmodel.{ ConcertItem, ConcertRepository }

import java.sql.Timestamp
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.duration.{ DurationInt, FiniteDuration }

/** RelationalDatabase を使う ConcertRepository の 共通テスト を定義する
  *
  * テスト共通化のため、このような形式でテストケースを実装する。
  *
  *  テスト共通化の方法については、
  * [[https://www.scalatest.org/user_guide/sharing_tests Sharing tests]]
  * を参照すること
  */
trait DatabaseConcertRepositoryBehaviors { this: DatabaseConcertRepositorySpecBase =>
  import DatabaseConcertRepositoryBehaviors._

  private val idGenerator = new ConcertIdGenerator()

  // UUIDv1 を生成する標準機能がないので,
  // https://www.uuidgenerator.net/version1
  // で 10 個生成した。0 から古い順で並んでいる。
  private val UUIDv1: Vector[UUID] = {
    """
      |1ee6917e-ed02-11ea-adc1-0242ac120002
      |1ee69548-ed02-11ea-adc1-0242ac120002
      |1ee69656-ed02-11ea-adc1-0242ac120002
      |1ee69728-ed02-11ea-adc1-0242ac120002
      |1ee697fa-ed02-11ea-adc1-0242ac120002
      |1ee698c2-ed02-11ea-adc1-0242ac120002
      |1ee6998a-ed02-11ea-adc1-0242ac120002
      |1ee69a52-ed02-11ea-adc1-0242ac120002
      |1ee69b1a-ed02-11ea-adc1-0242ac120002
      |1ee69d4a-ed02-11ea-adc1-0242ac120002
      |""".stripMargin.split("\n").filter(_.nonEmpty).map(UUID.fromString).toVector
  }

  // http://doc.scalatest.org/3.1.0/org/scalatest/wordspec/AnyWordSpec.html
  // See shared tests
  def databaseConcertRepository(repository: ConcertRepository, databaseService: ConcertDatabaseService): Unit = {

    def concertOf(id: ConcertId): databaseService.ConcertRow = {
      import databaseService._
      import databaseService.profile.api._
      databaseService.database.run(concerts.filter(_.id === id.value).result.head).futureValue
    }

    "be updated by ConcertCreated event" in {
      val id                       = idGenerator.nextId()
      val initialNumOfTickets      = 3
      val createdEventOccurredTime = nowInSeconds
      val createdEventOffset       = TimeBasedUUID(UUIDv1(0))

      repository
        .updateByConcertEvent(
          ConcertCreated(id, initialNumOfTickets, createdEventOccurredTime),
          createdEventOffset,
        ).futureValue

      val concertRow = concertOf(id)
      concertRow.numberOfTickets shouldBe initialNumOfTickets
      concertRow.cancelled shouldBe false
      concertRow.createdAt.toInstant shouldBe createdEventOccurredTime.toInstant
      concertRow.updatedAt.toInstant shouldBe createdEventOccurredTime.toInstant

      val offsetRow = repository.fetchConcertEventOffset().futureValue
      offsetRow shouldBe createdEventOffset
    }

    "be updated by ConcertTicketBought event" in {
      val id = idGenerator.nextId()

      val initialNumOfTickets         = 3
      val boughtTickets               = (1 to 2).map(ConcertTicketId).toVector
      val expectRemainingNumOfTickets = 1

      assume(boughtTickets.size <= initialNumOfTickets)
      assume(expectRemainingNumOfTickets == initialNumOfTickets - boughtTickets.size)

      val createdOccurredTime     = nowInSeconds
      val createdEventOffset      = TimeBasedUUID(UUIDv1(0))
      val boughtEventOccurredTime = createdOccurredTime + 1.second
      val boughtEventOffset       = TimeBasedUUID(UUIDv1(1))

      assume(boughtEventOccurredTime.isAfter(createdOccurredTime))
      assume(boughtEventOffset > createdEventOffset)

      repository
        .updateByConcertEvent(
          ConcertCreated(id, initialNumOfTickets, createdOccurredTime),
          createdEventOffset,
        ).futureValue
      repository
        .updateByConcertEvent(
          ConcertTicketsBought(id, boughtTickets, boughtEventOccurredTime),
          boughtEventOffset,
        ).futureValue

      val concertRow = concertOf(id)
      concertRow.numberOfTickets shouldBe expectRemainingNumOfTickets
      concertRow.cancelled shouldBe false
      concertRow.createdAt.toInstant shouldBe createdOccurredTime.toInstant
      concertRow.updatedAt.toInstant shouldBe boughtEventOccurredTime.toInstant

      val offsetRow = repository.fetchConcertEventOffset().futureValue
      offsetRow shouldBe boughtEventOffset
    }

    "be updated by ConcertCancelled event" in {
      val id                  = idGenerator.nextId()
      val initialNumOfTickets = 3

      val createdEventOccurredTime   = nowInSeconds
      val createdEventOffset         = TimeBasedUUID(UUIDv1(0))
      val cancelledEventOccurredTime = createdEventOccurredTime + 1.second
      val cancelledEventOffset       = TimeBasedUUID(UUIDv1(1))

      assume(cancelledEventOccurredTime.isAfter(createdEventOccurredTime))
      assume(cancelledEventOffset > createdEventOffset)

      repository
        .updateByConcertEvent(
          ConcertCreated(id, initialNumOfTickets, createdEventOccurredTime),
          createdEventOffset,
        ).futureValue
      repository
        .updateByConcertEvent(
          ConcertCancelled(id, cancelledEventOccurredTime),
          cancelledEventOffset,
        ).futureValue

      val concertRow = concertOf(id)
      concertRow.numberOfTickets shouldBe initialNumOfTickets
      concertRow.cancelled shouldBe true
      concertRow.createdAt.toInstant shouldBe createdEventOccurredTime.toInstant
      concertRow.updatedAt.toInstant shouldBe cancelledEventOccurredTime.toInstant

      val offsetRow = repository.fetchConcertEventOffset().futureValue
      offsetRow shouldBe cancelledEventOffset
    }

    "fetch concerts ordered by updated date" in {

      val now = nowInSeconds
      val id1 = idGenerator.nextId()
      val id2 = idGenerator.nextId()
      val id3 = idGenerator.nextId()

      // Setup
      // cancelled でフィルタされていないことがわかるようにする
      // updatedAt ソートされることがわかるようにする
      {
        import databaseService._
        import databaseService.profile.api._
        val rows = Seq(
          ConcertRow(
            id = id1.value,
            numberOfTickets = 3,
            cancelled = true,
            createdAt = now.toSQLTimestamp,
            updatedAt = (now + 3.seconds).toSQLTimestamp,
          ),
          ConcertRow(
            id = id2.value,
            numberOfTickets = 1,
            cancelled = false,
            createdAt = (now + 1.second).toSQLTimestamp,
            updatedAt = (now + 5.seconds).toSQLTimestamp,
          ),
          ConcertRow(
            id = id3.value,
            numberOfTickets = 2,
            cancelled = false,
            createdAt = (now + 2.seconds).toSQLTimestamp,
            updatedAt = (now + 4.seconds).toSQLTimestamp,
          ),
        )
        Await.result(database.run(concerts ++= rows), timeout.duration)
      }

      // Check
      repository.fetchConcertList(excludeCancelled = false).futureValue shouldBe Seq(
        ConcertItem(id2, numberOfTickets = 1, cancelled = false),
        ConcertItem(id3, numberOfTickets = 2, cancelled = false),
        ConcertItem(id1, numberOfTickets = 3, cancelled = true),
      )

    }

    "fetch non-cancelled concerts with excludeCancelled flag" in {
      val now = nowInSeconds
      val id1 = idGenerator.nextId()
      val id2 = idGenerator.nextId()
      val id3 = idGenerator.nextId()

      // cancelled でフィルタされることがわかるようにする
      {
        import databaseService._
        import databaseService.profile.api._
        val rows = Seq(
          ConcertRow(
            id = id1.value,
            numberOfTickets = 3,
            cancelled = true,
            createdAt = now.toSQLTimestamp,
            updatedAt = (now + 3.seconds).toSQLTimestamp,
          ),
          ConcertRow(
            id = id2.value,
            numberOfTickets = 1,
            cancelled = false,
            createdAt = (now + 1.second).toSQLTimestamp,
            updatedAt = (now + 5.seconds).toSQLTimestamp,
          ),
          ConcertRow(
            id = id3.value,
            numberOfTickets = 2,
            cancelled = false,
            createdAt = (now + 2.seconds).toSQLTimestamp,
            updatedAt = (now + 4.seconds).toSQLTimestamp,
          ),
        )
        Await.result(database.run(concerts ++= rows), timeout.duration)
      }

      repository.fetchConcertList(excludeCancelled = true).futureValue shouldBe Seq(
        ConcertItem(id2, numberOfTickets = 1, cancelled = false),
        ConcertItem(id3, numberOfTickets = 2, cancelled = false),
      )

    }

  }

  // 現在時刻を秒精度で返す。
  // 秒未満はデータベースによってデフォルト値やサポート範囲が異なるため秒精度に丸める。
  // https://mariadb.com/kb/en/timestamp/#supported-values
  // https://www.h2database.com/html/datatypes.html#timestamp_type
  private def nowInSeconds: ZonedDateTime = {
    ZonedDateTime.now.truncatedTo(ChronoUnit.SECONDS)
  }

}

object DatabaseConcertRepositoryBehaviors {

  implicit final class RichZonedDateTime(val time: ZonedDateTime) extends AnyVal {
    def +(duration: FiniteDuration): ZonedDateTime = {
      time.plusNanos(duration.toNanos)
    }
    def toSQLTimestamp: Timestamp = {
      Timestamp.from(time.toInstant)
    }
  }

}
