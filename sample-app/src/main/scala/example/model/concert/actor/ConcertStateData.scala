package example.model.concert.actor

import example.model.KryoSerializable
import example.model.concert._

/** ConcertActor の状態データ。
  * シリアライズされる。
  */
sealed trait ConcertStateData extends KryoSerializable

/** Concertなし
  */
case class NoConcertStateData() extends ConcertStateData

/** Concertあり
  */
case class AvailableConcertStateData(
    tickets: Vector[ConcertTicketId],
) extends ConcertStateData

/** Concertあり(キャンセル済み)
  */
case class CancelledConcertStateData(
    tickets: Vector[ConcertTicketId],
) extends ConcertStateData
