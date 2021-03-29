package example.readmodel.rdb

import java.util.UUID

import akka.persistence.query.{ NoOffset, Offset, TimeBasedUUID }

import scala.concurrent.ExecutionContext

trait UpdaterOffsetQuerySupport {
  protected val databaseService: ConcertDatabaseService

  import databaseService._
  import databaseService.profile.api._

  // イベント種類(tagName)と進捗情報(OFFSET)を保存する DBIO を構築する
  protected def insertOrUpdateOffset(tagName: String, offset: Offset): DBIO[Int] = {
    offset match {
      case TimeBasedUUID(uuid) =>
        updaterOffsets insertOrUpdate UpdaterOffsetRow(tagName, uuid.toString)
      case other =>
        // サポートしないためエラーとする
        DBIO.failed(new IllegalArgumentException("TimeBasedUUID is only supported."))
    }
  }

  // イベント種類(tagName) から保存済み進捗情報を取得する DBIO を構築する
  protected def fetchOffset(tagName: String)(implicit executionContext: ExecutionContext): DBIO[Offset] = {
    for {
      offsetOption <-
        updaterOffsets
          .filter(_.tagName === tagName)
          .map(_.offsetUUID)
          .result
          .headOption
    } yield {
      offsetOption.fold[Offset](NoOffset) { offsetString =>
        val uuid = UUID.fromString(offsetString)
        TimeBasedUUID(uuid)
      }
    }
  }
}
