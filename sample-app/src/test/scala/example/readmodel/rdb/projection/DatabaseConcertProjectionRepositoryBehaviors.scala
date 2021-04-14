package example.readmodel.rdb.projection

import example.model.concert.ConcertEvent.{ ConcertCancelled, ConcertCreated, ConcertTicketsBought }
import example.model.concert.{ ConcertId, ConcertIdGenerator, ConcertTicketId }
import example.readmodel.rdb.{ ConcertDatabaseService, DatabaseSpecBase }
import slick.dbio.DBIO

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

/** ConcertProjectionRepository の 共通テスト を定義する
  *
  * テスト共通化のため、このような形式でテストケースを実装する。
  *
  *  テスト共通化の方法については、
  * [[https://www.scalatest.org/user_guide/sharing_tests Sharing tests]]
  * を参照すること
  */
trait DatabaseConcertProjectionRepositoryBehaviors { this: DatabaseSpecBase =>
  import DatabaseSpecBase._

  private val idGenerator = new ConcertIdGenerator()

  def databaseConcertProjectionRepository(
      projectionRepository: ConcertProjectionRepository,
      databaseService: ConcertDatabaseService,
  ): Unit = {

    def concertOf(id: ConcertId): databaseService.ConcertRow = {
      import databaseService._
      import databaseService.profile.api._
      databaseService.database.run(concerts.filter(_.id === id.value).result.head).futureValue
    }

    def runDBIO[T](action: slick.dbio.DBIO[T]): T = {
      import databaseService._
      Await.result(database.run(action), timeout.duration)
    }

    "update a database using ConcertCreated event" in {
      val id                       = idGenerator.nextId()
      val initialNumOfTickets      = 3
      val createdEventOccurredTime = nowInSeconds

      runDBIO {
        projectionRepository.update(
          ConcertCreated(id, initialNumOfTickets, createdEventOccurredTime),
        )
      }

      val concertRow = concertOf(id)
      concertRow.numberOfTickets shouldBe initialNumOfTickets
      concertRow.cancelled shouldBe false
      concertRow.createdAt.toInstant shouldBe createdEventOccurredTime.toInstant
      concertRow.updatedAt.toInstant shouldBe createdEventOccurredTime.toInstant

    }

    "update a database using ConcertTicketBought event" in {
      val id                          = idGenerator.nextId()
      val initialNumOfTickets         = 3
      val boughtTickets               = (1 to 2).map(ConcertTicketId).toVector
      val expectRemainingNumOfTickets = 1

      assume(boughtTickets.size <= initialNumOfTickets)
      assume(expectRemainingNumOfTickets == initialNumOfTickets - boughtTickets.size)

      val createdOccurredTime     = nowInSeconds
      val boughtEventOccurredTime = createdOccurredTime + 1.second

      assume(boughtEventOccurredTime.isAfter(createdOccurredTime))

      runDBIO {
        DBIO.seq(
          projectionRepository.update(ConcertCreated(id, initialNumOfTickets, createdOccurredTime)),
          projectionRepository.update(ConcertTicketsBought(id, boughtTickets, boughtEventOccurredTime)),
        )
      }

      val concertRow = concertOf(id)
      concertRow.numberOfTickets shouldBe expectRemainingNumOfTickets
      concertRow.cancelled shouldBe false
      concertRow.createdAt.toInstant shouldBe createdOccurredTime.toInstant
      concertRow.updatedAt.toInstant shouldBe boughtEventOccurredTime.toInstant

    }

    "update a database using ConcertCancelled event" in {
      val id                  = idGenerator.nextId()
      val initialNumOfTickets = 3

      val createdEventOccurredTime   = nowInSeconds
      val cancelledEventOccurredTime = createdEventOccurredTime + 1.second

      assume(cancelledEventOccurredTime.isAfter(createdEventOccurredTime))

      runDBIO {
        DBIO.seq(
          projectionRepository.update(ConcertCreated(id, initialNumOfTickets, createdEventOccurredTime)),
          projectionRepository.update(ConcertCancelled(id, cancelledEventOccurredTime)),
        )
      }

      val concertRow = concertOf(id)
      concertRow.numberOfTickets shouldBe initialNumOfTickets
      concertRow.cancelled shouldBe true
      concertRow.createdAt.toInstant shouldBe createdEventOccurredTime.toInstant
      concertRow.updatedAt.toInstant shouldBe cancelledEventOccurredTime.toInstant

    }

  }

}
