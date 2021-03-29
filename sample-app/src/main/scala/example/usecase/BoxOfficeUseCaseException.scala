package example.usecase

import example.model.concert.ConcertError

/** BoxOffice の UseCase で発生した例外を表す。
  * @param message
  */
final class BoxOfficeUseCaseException private (message: String) extends Exception(message) {
  def this(cause: ConcertError) = {
    this(cause.toString)
  }
}
