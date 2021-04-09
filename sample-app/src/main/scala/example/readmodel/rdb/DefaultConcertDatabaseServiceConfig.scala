package example.readmodel.rdb

import akka.actor.typed.ActorSystem
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

/** データベース設定用 (DIで意図しないクラスが入らないように専用クラスでラップする)
  * @param databaseConfig
  */
final class DefaultConcertDatabaseServiceConfig(
    val databaseConfig: DatabaseConfig[JdbcProfile],
)

object DefaultConcertDatabaseServiceConfig {
  def apply(system: ActorSystem[Nothing]): DefaultConcertDatabaseServiceConfig = {
    val config = DatabaseConfig.forConfig[JdbcProfile]("example.database.concert", system.settings.config)
    new DefaultConcertDatabaseServiceConfig(config)
  }
}
