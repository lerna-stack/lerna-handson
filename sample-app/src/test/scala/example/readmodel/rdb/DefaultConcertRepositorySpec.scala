package example.readmodel.rdb

import akka.actor.typed.ActorSystem
import example.model.concert.ConcertIdGenerator
import example.readmodel.{ ConcertItem, ConcertRepository, DefaultReadModelDiDesign }
import testkit.AirframeDiSessionSupport
import wvlet.airframe.Design

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

final class DefaultConcertRepositorySpec extends DatabaseSpecBase() with AirframeDiSessionSupport {

  import DatabaseSpecBase._

  override protected val design: Design =
    DefaultReadModelDiDesign.design
      .bind[ActorSystem[Nothing]].toInstance(system)
      .bind[DefaultConcertDatabaseServiceConfig].toInstance(
        new DefaultConcertDatabaseServiceConfig(databaseConfig),
      )

  protected override val databaseService: ConcertDatabaseService = session.build[ConcertDatabaseService]

  private val idGenerator                   = new ConcertIdGenerator()
  private val repository: ConcertRepository = session.build[DefaultConcertRepository]

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
