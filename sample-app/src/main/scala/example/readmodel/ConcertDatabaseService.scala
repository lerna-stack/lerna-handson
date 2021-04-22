package example.readmodel

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

/** ConcertDatabaseを扱うサービスクラス
  */
trait ConcertDatabaseService extends ConcertDatabaseTables with AutoCloseable {
  val databaseConfig: DatabaseConfig[JdbcProfile]
  val database: JdbcProfile#Backend#Database

  override def close(): Unit = {
    database.close()
  }

}
