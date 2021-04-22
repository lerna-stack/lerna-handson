package example.application.command

import example.adapter.ConcertError

final class BoxOfficeServiceException private (message: String) extends Exception(message) {
  def this(cause: ConcertError) = {
    this(cause.toString)
  }
}
