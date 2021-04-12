package example.application.http.protocol

import example.model.concert.{ ConcertId, ConcertTicketId }
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import ConcertJsonProtocol._
import example.model.concert.service.BoxOfficeService.BuyConcertTicketsResponse

/** コンサートチケット購入 レスポンス
  *
  * @param id コンサートID
  * @param tickets 購入したチケットID
  */
case class BuyConcertTicketsResponseBody(id: ConcertId, tickets: Vector[ConcertTicketId])
object BuyConcertTicketsResponseBody {

  implicit val BuyConcertTicketsResponseBodyJsonFormat: RootJsonFormat[BuyConcertTicketsResponseBody] =
    jsonFormat2(BuyConcertTicketsResponseBody.apply)

  def from(id: ConcertId, response: BuyConcertTicketsResponse): BuyConcertTicketsResponseBody = {
    BuyConcertTicketsResponseBody(id, response.tickets)
  }

}
