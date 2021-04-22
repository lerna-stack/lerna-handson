package example.application.projection

import akka.Done
import example.application.ConcertEvent
import slick.basic.DatabaseConfig
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

trait ConcertProjectionRepository {

  def config: DatabaseConfig[JdbcProfile]

  /** コンサートイベントをもとにレポジトリを更新する DBIO
    */
  def update(event: ConcertEvent): DBIO[Done]

}
