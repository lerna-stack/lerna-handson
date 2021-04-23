import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.language.postfixOps

// サンプルのため言語デフォルトのものを使っておくが、本番では使わないこと
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

object DBIOExample extends App {
  val config = DatabaseConfig.forConfig[JdbcProfile]("database")
  import config.profile.api._

  class Coffees(tag: Tag) extends Table[(String, Double)](tag, "COFFEES") {
    def name  = column[String]("NAME", O.PrimaryKey)
    def price = column[Double]("PRICE")
    def *     = (name, price)
  }
  val coffees = TableQuery[Coffees]

  // データベース
  val database: Database = config.db

  // 適当に初期化処理をしておく
  val setupFuture: Future[Unit] = database.run(
    DBIO.seq(
      coffees.schema.createIfNotExists,
      coffees += ("Moca"     -> 11.0),
      coffees += ("American" -> 4.0),
    ),
  )
  Await.ready(setupFuture, 1 second)

  // select "NAME" from "COFFEES"
  val action1: DBIO[Seq[String]] = coffees.map(_.name).result
  // select * from "COFFEES" where "PRICE" < 10.0
  val action2: DBIO[Seq[(String, Double)]] = coffees.filter(_.price < 10.0).result
  // select "NAME" from "COFFEES" order by "NAME" limit 1
  val action3: DBIO[String] = coffees.sortBy(_.name).map(_.name).take(1).result.head

  // DBIO の結果に対して処理をすることも可能である
  // この場合は action3 で取得した文字列を大文字に変換する
  val action4: DBIO[String] = action3.map(_.toUpperCase)

  // database.run で DBIO[T] を実行できる(非同期)。
  // Future[T] が戻ってくる。
  val result4Future: Future[String] = database.run(action4)
  val result4                       = Await.result(result4Future, 1 second)
  println(result4)

  // 実行結果を念のため確認しておく
  Await.ready(
    for {
      _ <- database.run(action1).map(println)
      _ <- database.run(action2).map(println)
      _ <- database.run(action3).map(println)
      _ <- database.run(action4).map(println)
    } yield (),
    1 second,
  )

  database.close()
}
