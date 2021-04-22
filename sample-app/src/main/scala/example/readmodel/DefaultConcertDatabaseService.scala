package example.readmodel

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

/** ConcertDatabaseServiceのデフォルト実装。
  */
final class DefaultConcertDatabaseService(config: DefaultConcertDatabaseServiceConfig) extends ConcertDatabaseService {
  override val profile                                     = config.databaseConfig.profile
  override val databaseConfig: DatabaseConfig[JdbcProfile] = config.databaseConfig
  override val database                                    = config.databaseConfig.db
}
