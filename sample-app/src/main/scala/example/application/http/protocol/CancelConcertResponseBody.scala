package example.application.http.protocol

import example.model.concert.ConcertId
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import ConcertJsonProtocol._
import example.model.concert.service.BoxOfficeService.CancelConcertResponse

/** コンサートキャンセル レスポンス
  *
  * @param id　コンサートID
  * @param tickets 残りチケット枚数
  */
case class CancelConcertResponseBody(id: ConcertId, tickets: Int)
object CancelConcertResponseBody {

  implicit val CancelConcertResponseBodyJsonFormat: RootJsonFormat[CancelConcertResponseBody] =
    jsonFormat2(CancelConcertResponseBody.apply)

  def from(id: ConcertId, response: CancelConcertResponse): CancelConcertResponseBody = {
    CancelConcertResponseBody(id, response.numberOfTickets)
  }

}
