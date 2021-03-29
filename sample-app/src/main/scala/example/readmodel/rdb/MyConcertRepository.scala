package example.readmodel.rdb

import java.sql.Timestamp

import akka.persistence.query.Offset
import example.model.concert._
import example.model.concert.ConcertEvent._
import example.readmodel._

import scala.concurrent._

/** 演習で実装する ConcertRepository
  */
final class MyConcertRepository(
    protected val databaseService: ConcertDatabaseService,
)(implicit
    executionContext: RepositoryExecutionContext,
) extends ConcertRepository
    with UpdaterOffsetQuerySupport {
  import databaseService._
  import databaseService.profile.api._

  /** コンサート一覧を取得する。
    */
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

  /** ConcertEvent のオフセットを取得する。存在しない場合は NoOffset を返す。
    */
  override def fetchConcertEventOffset(): Future[Offset] = {
    // 実装済み
    database.run(fetchOffset(ConcertEvent.tag))
  }

  /** コンサートイベントをもとにレポジトリを更新する。
    */
  override def updateByConcertEvent(event: ConcertEvent, offset: Offset): Future[Unit] = {
    // イベントをもとに DBIO を構築する。
    val updateByEventDBIO: DBIO[AnyVal] = event match {
      case created: ConcertCreated =>
        handleConcertCreatedDBIO(created)
      case cancelled: ConcertCancelled =>
        handleConcertCancelledDBIO(cancelled)
      case ticketsBought: ConcertTicketsBought =>
        handleConcertTicketsBoughtDBIO(ticketsBought)
    }
    // CONCERTSテーブル更新 と 進捗(OFFSET)の保存処理を DBIO で合成する。
    val updateDBIO: DBIO[Unit] = for {
      _ <- updateByEventDBIO
      _ <- insertOrUpdateOffset(ConcertEvent.tag, offset)
    } yield ()

    // 合成した DBIO をトランザクションの中で実行する。
    database.run(updateDBIO.transactionally)
  }

  // 演習で実装する項目
  // コンサート作成イベントをもとに DBIO を構築する。
  // このメソッドを実行しても 実際に DB にクエリが発行されるわけではないので注意すること
  private def handleConcertCreatedDBIO(event: ConcertCreated): DBIO[Int] = {
    // 新しいコンサートのエントリを追加する DBIO を構築する
    // insert into "CONCERTS" ("ID", "NUMBER_OF_TICKETS", "CANCELLED", "CREATED_AT", "UPDATED_AT") values (?,?,?,?,?)
    ???
  }

  // 演習で実装する項目
  // コンサートキャンセルイベントをもとに DBIO を構築する。
  // このメソッドを実行しても 実際に DB にクエリが発行されるわけではないので注意すること
  private def handleConcertCancelledDBIO(event: ConcertCancelled): DBIO[Int] = {
    // 対応するコンサートの　キャンセル済 CANCELLED と 更新日時 UPDATED_AT を更新する DBIO を構築する
    // update "CONCERTS" set "CANCELLED" = ?, "UPDATED_AT" = ? where "CONCERTS"."ID" = ?
    ???
  }

  // コンサートチケット購入イベントをもとに DBIO を構築する。
  // このメソッドを実行しても 実際に DB にクエリが発行されるわけではないので注意すること
  private def handleConcertTicketsBoughtDBIO(event: ConcertTicketsBought): DBIO[Unit] = {
    // 購入前のチケット枚数を読み込み、購入後のチケット枚数を計算して格納するクエリ
    val updateByBoughtEventDBIO: DBIO[Unit] = for {
      // 購入前のチケット枚数を取得する
      // select "NUMBER_OF_TICKET" from "CONCERTS" where "ID" = ?
      numOfTickets <-
        concerts
          .filter(_.id === event.concertId.value)
          .map(item => item.numberOfTickets)
          .result.head
      // 新しいチケット枚数に更新する
      // update "CONCERTS" set "NUMBER_OF_TICKETS" = ?, "UPDATED_AT" = ? where "CONCERTS"."ID" = ?
      _ <-
        concerts
          .filter(_.id === event.concertId.value)
          .map(item => (item.numberOfTickets, item.updatedAt))
          .update((numOfTickets - event.tickets.size, Timestamp.from(event.occurredAt.toInstant)))
    } yield ()
    // このクエリはトランザクションの中で実行されなければいけない
    updateByBoughtEventDBIO.transactionally
  }

}
