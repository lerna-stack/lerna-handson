package example.readmodel

import akka.persistence.query.Offset
import example.model.concert.ConcertEvent

import scala.concurrent._

trait ConcertRepository {

  /** コンサート一覧を取得する。
    * @param excludeCancelled キャンセル済みを除外する
    */
  def fetchConcertList(excludeCancelled: Boolean): Future[Seq[ConcertItem]]

  /** ConcertEvent のオフセットを取得する。存在しない場合は NoOffset を返す。
    */
  def fetchConcertEventOffset(): Future[Offset]

  /** コンサートイベントをもとにレポジトリを更新する。
    */
  def updateByConcertEvent(event: ConcertEvent, offset: Offset): Future[Unit]
}
