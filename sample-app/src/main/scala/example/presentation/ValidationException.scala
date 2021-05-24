package example.presentation

import example.adapter.ConcertError

/** バリデーションに失敗した場合の例外を表現する。
  *
  * @param message
  */
final class ValidationException private (message: String) extends Exception(message) {
  def this(error: ConcertError) = {
    this(error.toString)
  }
}
