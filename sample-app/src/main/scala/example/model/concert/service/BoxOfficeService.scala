package example.model.concert.service

import example.model.concert.ConcertId
import example.model.concert.actor.ConcertActor._

import scala.concurrent.Future

trait BoxOfficeService {

  /** コンサートを作成する。
    */
  def createConcert(id: ConcertId, numberOfTickets: Int): Future[CreateResponse]

  /** コンサートを取得する。
    */
  def getConcert(id: ConcertId): Future[GetResponse]

  /** コンサートをキャンセルする。
    */
  def cancelConcert(id: ConcertId): Future[CancelResponse]

  /** コンサートのチケットを購入する。
    */
  def buyConcertTickets(id: ConcertId, numberOfTickets: Int): Future[BuyTicketsResponse]
}
