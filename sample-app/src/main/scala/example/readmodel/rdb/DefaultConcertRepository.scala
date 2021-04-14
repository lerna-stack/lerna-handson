package example.readmodel.rdb

import akka.persistence.query.Offset
import example.model.concert._
import example.readmodel._
import example.readmodel.rdb.projection.ConcertProjectionRepository

import scala.concurrent._

/** ConcertRepository のデフォルト実装
  */
final class DefaultConcertRepository(
    protected val databaseService: ConcertDatabaseService,
    projectionRepository: ConcertProjectionRepository, // TODO remove
)(implicit
    executionContext: RepositoryExecutionContext,
) extends ConcertRepository
    with UpdaterOffsetQuerySupport {
  import databaseService._
  import databaseService.profile.api._

  override def fetchConcertList(excludeCancelled: Boolean): Future[Seq[ConcertItem]] = {
    // コンサート一覧を取得する DBIO を構築する
    // select ("ID", "NUMBER_OF_TICKETS", "CANCELLED") from "CONCERTS" order by "UPDATED_AT" desc
    // or
    // select ("ID", "NUMBER_OF_TICKETS", "CANCELLED") from "CONCERTS" where "CANCELLED" != true order by "UPDATED_AT" desc
    val fetchRawDBIO: DBIO[Seq[(String, Int, Boolean)]] = concerts
      .filterIf(excludeCancelled)(_.cancelled =!= true)
      .sortBy(_.updatedAt.desc)
      .map(item => (item.id, item.numberOfTickets, item.cancelled))
      .result

    // DB から取り出した 行を ConcertItem に変換する処理
    def toConcertItem(rawItem: (String, Int, Boolean)): ConcertItem = {
      // ConcertId に変換できない文字列が DB に格納されている場合は、
      // DBが不正な状態になっている可能性が高いのでエラーを投げる
      val id = ConcertId.fromString(rawItem._1).left.map(error => new IllegalStateException(error.toString)).toTry.get
      ConcertItem(id, rawItem._2, rawItem._3)
    }

    // DB から取り出した行それぞれに対して、toConcertItem で変換処理をする DBIO を 構築する。
    val fetchDBIO: DBIO[Seq[ConcertItem]] = fetchRawDBIO.map(_.map(toConcertItem))

    // 実際に DBIO を発行する
    database.run(fetchDBIO)
  }

  override def fetchConcertEventOffset(): Future[Offset] = {
    // ConcertEventの進捗情報(OFFSET)を取得するクエリを発行する
    database.run(fetchOffset(ConcertEvent.tag))
  }

  override def updateByConcertEvent(event: ConcertEvent, offset: Offset): Future[Unit] = {
    // CONCERTSテーブル更新 と 進捗(OFFSET)の保存処理を DBIO で合成する。
    val updateDBIO: DBIO[Unit] = for {
      _ <- projectionRepository.update(event)
      _ <- insertOrUpdateOffset(ConcertEvent.tag, offset)
    } yield ()

    // 合成した DBIO をトランザクションの中で実行する。
    database.run(updateDBIO.transactionally)
  }

}
