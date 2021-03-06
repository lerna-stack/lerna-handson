package example.presentation.protocol

import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._
import ConcertJsonProtocol._
import example.adapter.ConcertId
import example.adapter.command.BoxOfficeService.GetConcertResponse

/** コンサート取得 レスポンス
  *
  * @param id コンサートID
  * @param tickets 残りチケット枚数
  * @param cancelled キャンセル済みかどうか
  */
case class GetConcertResponseBody(id: ConcertId, tickets: Int, cancelled: Boolean)

object GetConcertResponseBody {

  implicit val GetConcertResponseBodyJsonFormat: RootJsonFormat[GetConcertResponseBody] =
    jsonFormat3(GetConcertResponseBody.apply)

  def from(concertId: ConcertId, response: GetConcertResponse): GetConcertResponseBody = {
    GetConcertResponseBody(concertId, response.tickets.size, response.cancelled)
  }

}
