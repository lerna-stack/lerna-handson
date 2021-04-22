package example.application.projection

import akka.Done
import example.application.ConcertEvent.{ ConcertCancelled, ConcertCreated, ConcertTicketsBought }
import example.application.{ ApplicationExecutionContext, ConcertEvent }
import example.readmodel.ConcertDatabaseService
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import java.sql.Timestamp

final class MyConcertProjectionRepository(
    databaseService: ConcertDatabaseService,
)(implicit executionContext: ApplicationExecutionContext)
    extends ConcertProjectionRepository {

  import databaseService._
  import databaseService.profile.api._

  override def config: DatabaseConfig[JdbcProfile] = databaseService.databaseConfig

  /** コンサートイベントをもとにレポジトリを更新する DBIO
    */
  override def update(event: ConcertEvent): slick.dbio.DBIO[Done] = {
    event match {
      case created: ConcertCreated =>
        handleConcertCreatedDBIO(created).map(_ => Done)
      case cancelled: ConcertCancelled =>
        handleConcertCancelledDBIO(cancelled).map(_ => Done)
      case bought: ConcertTicketsBought =>
        handleConcertTicketsBoughtDBIO(bought)
    }
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
  private def handleConcertTicketsBoughtDBIO(event: ConcertTicketsBought): DBIO[Done] = {
    // 購入前のチケット枚数を読み込み、購入後のチケット枚数を計算して格納するクエリ
    for {
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
    } yield Done
  }

}
