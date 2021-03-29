package example.readmodel.rdb

import slick.jdbc.JdbcProfile

/** ConcertDatabaseを扱うサービスクラス
  */
trait ConcertDatabaseService extends ConcertDatabaseTables {
  val database: JdbcProfile#Backend#Database
}
