package example.model.concert.service

import example.model.concert.{ ConcertId, ConcertTicketId }

import scala.concurrent.Future

object BoxOfficeService {

  /** コンサート作成完了
    * @param numberOfTickets
    */
  final case class CreateConcertResponse(numberOfTickets: Int)

  /** コンサート取得完了
    * @param tickets 残りチケット
    * @param cancelled キャンセル済みか
    */
  final case class GetConcertResponse(tickets: Vector[ConcertTicketId], cancelled: Boolean)

  /** コンサートキャンセル完了
    * @param numberOfTickets 残りチケット枚数
    */
  final case class CancelConcertResponse(numberOfTickets: Int)

  /** コンサートチケット購入完了
    * @param tickets 購入したチケット
    */
  final case class BuyConcertTicketsResponse(tickets: Vector[ConcertTicketId])

}

trait BoxOfficeService {
  import BoxOfficeService._

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
