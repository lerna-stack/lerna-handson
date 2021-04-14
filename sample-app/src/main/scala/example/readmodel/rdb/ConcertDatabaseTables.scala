package example.readmodel.rdb

import java.sql.Timestamp

import slick.jdbc.JdbcProfile

/** ConcertDatabase のテーブル定義 (手動生成)
  * プロダクトでは
  *  - slick-codegen などで自動生成したほうがよい
  *  - ID は String ではなく INTEGER などの高速なものを使うほうがよい
  *  - 実行するクエリをもとにIndexを作成するほうがよい
  */
trait ConcertDatabaseTables {
  val profile: JdbcProfile

  import profile.api._

  case class ConcertRow(
      id: String,
      numberOfTickets: Int,
      cancelled: Boolean,
      createdAt: Timestamp,
      updatedAt: Timestamp,
  )
  final class ConcertTable(tag: Tag) extends Table[ConcertRow](tag, "CONCERTS") {
    def id              = column[String]("ID", O.PrimaryKey, O.Length(512, varying = true))
    def numberOfTickets = column[Int]("NUMBER_OF_TICKETS")
    def cancelled       = column[Boolean]("CANCELLED")
    def createdAt       = column[Timestamp]("CREATED_AT")
    def updatedAt       = column[Timestamp]("UPDATED_AT")
    def *               = (id, numberOfTickets, cancelled, createdAt, updatedAt).mapTo[ConcertRow]
  }
  val concerts = TableQuery[ConcertTable]
}
