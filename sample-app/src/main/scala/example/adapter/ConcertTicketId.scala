package example.adapter

/** コンサートチケットID。
  * シリアライズされる。
  * 簡易化のため、バリデーションは行わない。
  */
final case class ConcertTicketId(value: Int) extends AnyVal
