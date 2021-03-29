package example.application.http.controller

import example.model.concert.ConcertError

/** Controller でバリデーションに失敗した場合の例外を表現する。
  * @param message
  */
final class ValidationException private (message: String) extends Exception(message) {
  def this(error: ConcertError) = {
    this(error.toString)
  }
}
