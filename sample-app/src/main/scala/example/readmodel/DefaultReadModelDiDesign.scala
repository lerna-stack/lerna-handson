package example.readmodel

import akka.actor.typed.ActorSystem
import example.readmodel.rdb._
import example.readmodel.rdb.projection._
import wvlet.airframe._

/** RDBを使った ReadModel のデフォルト実装のDIデザイン
  */
object DefaultReadModelDiDesign {
  // format: off
  lazy val design: Design =
    newDesign
      .bind[ConcertDatabaseService].to[DefaultConcertDatabaseService]
      .bind[DefaultConcertDatabaseServiceConfig].toSingletonProvider[ActorSystem[Nothing]] { system =>
        DefaultConcertDatabaseServiceConfig(system)
      }
      .bind[RepositoryExecutionContext].toSingletonProvider[ActorSystem[Nothing]] { system =>
        system.executionContext // TODO RDB用の ExecutionContxt を提供する。
      }
      .bind[ConcertProjectionRepository].to[DefaultConcertProjectionRepository]
      .bind[ConcertRepository].to[DefaultConcertRepository]
}
