package example.readmodel.rdb.projection

import akka.Done
import example.model.concert.ConcertEvent
import slick.basic.DatabaseConfig
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

trait ConcertProjectionRepository {

  def config: DatabaseConfig[JdbcProfile]

  /** コンサートイベントをもとにレポジトリを更新する DBIO
    */
  def update(event: ConcertEvent): DBIO[Done]

}
