package example.adapter

import scala.concurrent.Future

object ConcertRepository {

  /** コンサート一覧項目
    *
    * @param id コンサートID
    * @param numberOfTickets 残りチケット枚数
    * @param cancelled キャンセル済みか
    */
  case class ConcertItem(id: ConcertId, numberOfTickets: Int, cancelled: Boolean)

}

trait ConcertRepository {
  import ConcertRepository._

  /** コンサート一覧を取得する。
    * @param excludeCancelled キャンセル済みを除外する
    */
  def fetchConcertList(excludeCancelled: Boolean): Future[Seq[ConcertItem]]

}
