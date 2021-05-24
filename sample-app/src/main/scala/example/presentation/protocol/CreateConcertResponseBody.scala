package example.presentation.protocol

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import ConcertJsonProtocol._
import example.adapter.ConcertId
import example.adapter.command.BoxOfficeService.CreateConcertResponse

/** コンサート作成 レスポンス
  * @param id コンサートID
  * @param tickets 初期チケット枚数
  */
case class CreateConcertResponseBody(id: ConcertId, tickets: Int)
object CreateConcertResponseBody {

  implicit val CreateConcertResponseBodyJsonFormat: RootJsonFormat[CreateConcertResponseBody] =
    jsonFormat2(CreateConcertResponseBody.apply)

  def from(id: ConcertId, response: CreateConcertResponse): CreateConcertResponseBody = {
    CreateConcertResponseBody(id, response.numberOfTickets)
  }

}
