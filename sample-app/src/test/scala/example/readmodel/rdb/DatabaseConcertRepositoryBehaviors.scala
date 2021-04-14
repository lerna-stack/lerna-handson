package example.readmodel.rdb

import example.model.concert.ConcertIdGenerator
import example.readmodel.{ ConcertItem, ConcertRepository }

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

/** RelationalDatabase を使う ConcertRepository の 共通テスト を定義する
  *
  * テスト共通化のため、このような形式でテストケースを実装する。
  *
  *  テスト共通化の方法については、
  * [[https://www.scalatest.org/user_guide/sharing_tests Sharing tests]]
  * を参照すること
  */
trait DatabaseConcertRepositoryBehaviors { this: DatabaseConcertRepositorySpecBase =>
  import DatabaseConcertRepositorySpecBase._

  val idGenerator = new ConcertIdGenerator()

  def databaseConcertRepository(repository: ConcertRepository, databaseService: ConcertDatabaseService): Unit = {

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

}
