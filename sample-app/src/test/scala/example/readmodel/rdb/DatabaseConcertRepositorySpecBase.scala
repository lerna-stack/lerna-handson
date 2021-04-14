package example.readmodel.rdb

import com.typesafe.config.{ Config, ConfigFactory }
import example.ActorSpecBase
import org.scalatest.BeforeAndAfter
import org.scalatest.time.{ Milliseconds, Span }
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import scala.concurrent.duration.FiniteDuration

// TODO Rename to DatabaseSpecBase
abstract class DatabaseConcertRepositorySpecBase() extends ActorSpecBase() with BeforeAndAfter {
  // NOTE: すべての操作が H2DB 互換であり、H2DB でテストにパスすればよいと妥協している
  protected val config: Config = ConfigFactory.parseString("""
      |h2db {
      |  profile = "slick.jdbc.H2Profile$"
      |  db {
      |    url = "jdbc:h2:mem:concert"
      |    driver = org.h2.Driver
      |    connectionPool = disabled
      |    keepAliveConnection = true
      |  }
      |}
      |""".stripMargin)

  protected val databaseConfig: DatabaseConfig[JdbcProfile] =
    DatabaseConfig.forConfig("h2db", config)

  protected val databaseService: ConcertDatabaseService

  // FIXME Define global patience config
  override implicit lazy val patienceConfig: PatienceConfig = PatienceConfig(Span(1000L, Milliseconds))

  override def beforeAll(): Unit = {
    super.beforeAll()

    import databaseService._
    import databaseService.profile.api._
    val schema = concerts.schema ++ updaterOffsets.schema
    databaseService.database.run(schema.create).futureValue
  }

  before {
    // 各テストケースごとに独立してデータを処理したいため
    import databaseService._
    import databaseService.profile.api._
    val schema = concerts.schema ++ updaterOffsets.schema
    databaseService.database.run(schema.truncate).futureValue
  }

  // 現在時刻を秒精度で返す。
  // 秒未満はデータベースによってデフォルト値やサポート範囲が異なるため秒精度に丸める。
  // https://mariadb.com/kb/en/timestamp/#supported-values
  // https://www.h2database.com/html/datatypes.html#timestamp_type
  protected def nowInSeconds: ZonedDateTime = {
    ZonedDateTime.now.truncatedTo(ChronoUnit.SECONDS)
  }

}

object DatabaseConcertRepositorySpecBase {

  implicit final class RichZonedDateTime(val time: ZonedDateTime) extends AnyVal {
    def +(duration: FiniteDuration): ZonedDateTime = {
      time.plusNanos(duration.toNanos)
    }
    def toSQLTimestamp: Timestamp = {
      Timestamp.from(time.toInstant)
    }
  }

}
