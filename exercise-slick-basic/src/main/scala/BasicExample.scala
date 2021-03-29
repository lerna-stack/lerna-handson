import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps

object BasicExample extends App {
  // データベース設定のよみこみ
  val config = DatabaseConfig.forConfig[JdbcProfile]("database")
  import config.profile.api._

  // テーブル定義
  // データベースからコードを自動生成することもできる
  class Coffees(tag: Tag) extends Table[(String, Double)](tag, "COFFEES") {
    def name  = column[String]("NAME", O.PrimaryKey)
    def price = column[Double]("PRICE")
    def *     = (name, price)
  }
  val coffees = TableQuery[Coffees]

  // select NAME from COFFEES
  val query1 = coffees.map(_.name)

  // select * from COFFEES where PRICE < 10.0
  val query2 = coffees.filter(_.price < 10.0)

  // 実行されるSQL文を確認できる
  println(query1.result.statements)
  println(query2.result.statements)

  val database: Database = config.db

  val setupFuture: Future[Unit] = database.run(
    DBIO.seq(
      coffees.schema.createIfNotExists,
      coffees += ("Moca"     -> 11.0),
      coffees += ("American" -> 4.0),
    ),
  )
  Await.ready(setupFuture, 1 second)

  val namesFuture = database.run(query1.result)
  val names       = Await.result(namesFuture, 1 second)
  println(names)

  val lowPriceCoffeesFuture = database.run(query2.result)
  val lowPriceCoffees       = Await.result(lowPriceCoffeesFuture, 1 second)
  println(lowPriceCoffees)

  database.close()
}
