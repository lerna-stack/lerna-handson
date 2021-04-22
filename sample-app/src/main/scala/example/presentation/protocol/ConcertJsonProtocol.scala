package example.presentation.protocol

import example.adapter.{ ConcertId, ConcertTicketId }
import example.presentation.ValidationException
import spray.json.{ deserializationError, JsNumber, JsString, JsValue, JsonFormat }

/** domain.concert にある Value Object の JsonFormat 定義。
  * Json変換は、ドメインの関心ごとではないので domain.concert パッケージでは定義しないこと。
  * 定義する JsonFormat の数が増える場合は、ファイルを分割したほうが良い。
  */
object ConcertJsonProtocol {

  /** ConcertId の JsonFormat
    */
  implicit object ConcertIdJsonFormat extends JsonFormat[ConcertId] {
    def write(x: ConcertId) = JsString(x.value)
    def read(value: JsValue) = value match {
      case JsString(x) =>
        ConcertId.fromString(x).left.map(error => new ValidationException(error)).toTry.get
      case x => deserializationError("Expected ConcertId as JsString, but got " + x)
    }
  }

  /** ConcertTicketId の JsonFormat
    */
  implicit object ConcertTicketIdJsonFormat extends JsonFormat[ConcertTicketId] {
    override def write(x: ConcertTicketId): JsValue = JsNumber(x.value)
    override def read(value: JsValue): ConcertTicketId = value match {
      case JsNumber(x) => ConcertTicketId(x.intValue)
      case x           => deserializationError("Expected ConcertTicketId as JsNumber, but got " + x)
    }
  }

}
