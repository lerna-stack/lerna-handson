package example.application.http.controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.ExceptionHandler
import akka.http.scaladsl.server.Directives.complete
import example.usecase.BoxOfficeUseCaseException

/** BoxOffice に関連する例外を処理する。
  */
object DefaultBoxOfficeExceptionHandler {
  // すべて BadRequest として処理する。
  // メッセージは簡易的に文字列であるが、実際にはJSONなどで返すほうがよい。
  val handler: ExceptionHandler = ExceptionHandler {
    case exception: BoxOfficeUseCaseException =>
      complete(StatusCodes.BadRequest -> exception.toString)
    case exception: ValidationException =>
      complete(StatusCodes.BadRequest -> exception.toString)
  }
}
