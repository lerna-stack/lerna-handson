package example.readmodel.rdb

/** ConcertDatabaseServiceのデフォルト実装。
  */
final class DefaultConcertDatabaseService(config: DefaultConcertDatabaseServiceConfig)
    extends ConcertDatabaseService
    with AutoCloseable {
  override val profile = config.databaseConfig.profile

  override val database = config.databaseConfig.db

  override def close(): Unit = {
    database.close()
  }
}
