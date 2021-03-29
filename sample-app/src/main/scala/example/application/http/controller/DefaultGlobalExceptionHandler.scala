package example.application.http.controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.ExceptionHandler
import akka.http.scaladsl.server.Directives.complete

/** グローバルに例外を処理する。
  */
object DefaultGlobalExceptionHandler {
  val handler: ExceptionHandler = ExceptionHandler {
    case exception: Exception =>
      // すべての 例外(エラーではない) を InternalServerError として処理する。
      // メッセージは簡易的に文字列であるが、実際にはJSONなどで返すほうがよい。
      complete(StatusCodes.InternalServerError -> exception.toString)
  }
}
