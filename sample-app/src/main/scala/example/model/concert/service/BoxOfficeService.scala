package example.model.concert.service

import example.model.concert.actor.ConcertActorProtocol._

import scala.concurrent.Future

trait BoxOfficeService {

  /** コンサートを作成する。
    */
  def createConcert(request: CreateConcertRequest): Future[CreateConcertResponse]

  /** コンサートを取得する。
    */
  def getConcert(request: GetConcertRequest): Future[GetConcertResponse]

  /** コンサートをキャンセルする。
    */
  def cancelConcert(request: CancelConcertRequest): Future[CancelConcertResponse]

  /** コンサートのチケットを購入する。
    */
  def buyConcertTickets(request: BuyConcertTicketsRequest): Future[BuyConcertTicketsResponse]
}
