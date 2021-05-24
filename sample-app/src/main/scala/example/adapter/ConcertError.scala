package example.adapter

/** コンサートに関する業務エラー
  *
  * @param code
  * @param message
  * @param cause
  */
sealed abstract class ConcertError(val code: String, val message: String)

object ConcertError {
  private val prefix = "concert-error"

  /** 不正なコンサートID
    * @param message
    */
  final case class IllegalConcertIdError(override val message: String) extends ConcertError(s"$prefix-0010", message)

  /** 不正なコンサートチケットID
    * @param message
    */
  final case class IllegalConcertTicketIdError(override val message: String)
      extends ConcertError(s"$prefix-0020", message)

  /** 無効なコンサートの操作
    * @param message
    */
  final case class InvalidConcertOperationError(override val message: String)
      extends ConcertError(s"$prefix-0100", message)

  /** 指定されたコンサートが存在しない
    * @param concertId コンサートID
    */
  final case class ConcertNotFoundError(val concertId: ConcertId)
      extends ConcertError(s"$prefix-0200", s"Concert Not Found $concertId")

  /** 指定されたコンサートがすでに存在する
    * @param concertId コンサートID
    */
  final case class DuplicatedConcertError(val concertId: ConcertId)
      extends ConcertError(s"$prefix-0300", s"Concert Already Exists $concertId")
}
