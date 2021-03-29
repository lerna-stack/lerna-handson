package example.application.http.protocol

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

/** コンサートチケット購入 リクエスト
  *
  * @param tickets 購入チケット枚数
  */
case class BuyConcertTicketsRequestBody(tickets: Int)
object BuyConcertTicketsRequestBody {
  implicit val BuyConcertTicketsRequestBodyJsonFormat: RootJsonFormat[BuyConcertTicketsRequestBody] =
    jsonFormat1(BuyConcertTicketsRequestBody.apply)
}
