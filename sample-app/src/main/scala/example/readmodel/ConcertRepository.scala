package example.readmodel

import scala.concurrent._

trait ConcertRepository {

  /** コンサート一覧を取得する。
    * @param excludeCancelled キャンセル済みを除外する
    */
  def fetchConcertList(excludeCancelled: Boolean): Future[Seq[ConcertItem]]

}
