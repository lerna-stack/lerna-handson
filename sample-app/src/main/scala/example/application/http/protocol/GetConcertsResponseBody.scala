package example.application.http.protocol

import example.model.concert.ConcertId
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import ConcertJsonProtocol._
import example.readmodel.ConcertItem

/** コンサート一覧取得 レスポンス
  *
  * @param items コンサート一覧
  */
case class GetConcertsResponseBody(items: Vector[GetConcertsResponseBodyItem])
object GetConcertsResponseBody {

  import GetConcertsResponseBodyItem.GetConcertsResponseBodyItemJsonFormat

  implicit val GetConcertsResponseBodyJsonFormat: RootJsonFormat[GetConcertsResponseBody] =
    jsonFormat1(GetConcertsResponseBody.apply)

  def from(response: Seq[ConcertItem]): GetConcertsResponseBody = {
    GetConcertsResponseBody(
      response.map(GetConcertsResponseBodyItem.from).toVector,
    )
  }

}

/** コンサート一覧取得項目 レスポンス
  * @param id コンサートID
  * @param tickets 残りチケット枚数
  * @param cancelled　キャンセル済みかどうか
  */
case class GetConcertsResponseBodyItem(id: ConcertId, tickets: Int, cancelled: Boolean)
object GetConcertsResponseBodyItem {

  implicit val GetConcertsResponseBodyItemJsonFormat: RootJsonFormat[GetConcertsResponseBodyItem] =
    jsonFormat3(GetConcertsResponseBodyItem.apply)

  def from(response: ConcertItem): GetConcertsResponseBodyItem = {
    GetConcertsResponseBodyItem(response.id, response.numberOfTickets, response.cancelled)
  }

}
