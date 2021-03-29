package example.readmodel.rdb

import akka.actor.ActorSystem
import com.typesafe.config.{ Config, ConfigFactory }
import example.ActorSpecBase
import org.scalatest.BeforeAndAfter
import org.scalatest.time.{ Milliseconds, Span }
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

abstract class DatabaseConcertRepositorySpecBase(systemName: String)
    extends ActorSpecBase(ActorSystem(systemName))
    with BeforeAndAfter {
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

}
