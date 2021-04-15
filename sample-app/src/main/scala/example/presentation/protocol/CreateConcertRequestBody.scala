package example.presentation.protocol

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

/** コンサート作成 リクエストボディ
  *
  * @param tickets チケット枚数
  */
case class CreateConcertRequestBody(tickets: Int)
object CreateConcertRequestBody {
  implicit val CreateConcertRequestBodyJsonFormat: RootJsonFormat[CreateConcertRequestBody] =
    jsonFormat1(CreateConcertRequestBody.apply)
}
