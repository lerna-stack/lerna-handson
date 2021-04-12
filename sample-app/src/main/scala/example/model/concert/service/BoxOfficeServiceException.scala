package example.model.concert.service

import example.model.concert.ConcertError

final class BoxOfficeServiceException private (message: String) extends Exception(message) {
  def this(cause: ConcertError) = {
    this(cause.toString)
  }
}
