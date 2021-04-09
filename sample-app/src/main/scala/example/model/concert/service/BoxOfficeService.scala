package example.model.concert.service

import example.model.concert.ConcertId
import example.model.concert.actor.ConcertActor._

import scala.concurrent.Future

trait BoxOfficeService {

  /** コンサートを作成する。
    */
  def createConcert(id: ConcertId, numberOfTickets: Int): Future[CreateConcertResponse]

  /** コンサートを取得する。
    */
  def getConcert(id: ConcertId): Future[GetConcertResponse]

  /** コンサートをキャンセルする。
    */
  def cancelConcert(id: ConcertId): Future[CancelConcertResponse]

  /** コンサートのチケットを購入する。
    */
  def buyConcertTickets(id: ConcertId, numberOfTickets: Int): Future[BuyConcertTicketsResponse]
}
