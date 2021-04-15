package example.presentation

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.ExceptionHandler
import example.application.command.BoxOfficeServiceException

/** BoxOffice に関連する例外を処理する。
  */
object BoxOfficeExceptionHandler {
  // すべて BadRequest として処理する。
  // メッセージは簡易的に文字列であるが、実際にはJSONなどで返すほうがよい。
  val handler: ExceptionHandler = ExceptionHandler {
    case exception: BoxOfficeServiceException =>
      complete(StatusCodes.BadRequest -> exception.toString)
    case exception: ValidationException =>
      complete(StatusCodes.BadRequest -> exception.toString)
  }
}
