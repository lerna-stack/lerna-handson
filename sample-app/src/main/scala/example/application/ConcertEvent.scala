package example.application

import example.adapter.{ ConcertId, ConcertTicketId, KryoSerializable }

import java.time.ZonedDateTime

/** コンサートのイベントを表す
  * このイベントを実装するクラスはすべてシリアライズされる
  * イベントを表すクラス名,フィールド名,フィールド追加/変更/削除には最新の注意を払うこと
  */
sealed trait ConcertEvent extends KryoSerializable {
  val concertId: ConcertId
  val occurredAt: ZonedDateTime
}

object ConcertEvent {

  /** コンサートのイベントに付与するタグ
    */
  final val tag = "ConcertEvent"

  /** コンサートが作成されたことを示すイベント
    * @param concertId コンサートID
    * @param numOfTickets チケットの枚数
    * @param occurredAt いつ発生したか
    */
  final case class ConcertCreated(concertId: ConcertId, numOfTickets: Int, occurredAt: ZonedDateTime)
      extends ConcertEvent {
    require(numOfTickets > 0)
  }

  /** コンサートがキャンセルされたことを示すイベント
    * @param concertId コンサートID
    * @param occurredAt いつ発生したか
    */
  final case class ConcertCancelled(concertId: ConcertId, occurredAt: ZonedDateTime) extends ConcertEvent

  /** コンサートのチケットが購入されたことを示すイベント
    * @param concertId コンサートID
    * @param tickets 購入されたチケット
    * @param occurredAt いつ発生したか
    */
  final case class ConcertTicketsBought(
      concertId: ConcertId,
      tickets: Vector[ConcertTicketId],
      occurredAt: ZonedDateTime,
  ) extends ConcertEvent {
    require(tickets.size > 0)
  }
}
