package example.presentation

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.ExceptionHandler

/** グローバルに例外を処理する。
  */
object GlobalExceptionHandler {
  val handler: ExceptionHandler = ExceptionHandler {
    case exception: Exception =>
      // すべての 例外(エラーではない) を InternalServerError として処理する。
      // メッセージは簡易的に文字列であるが、実際にはJSONなどで返すほうがよい。
      complete(StatusCodes.InternalServerError -> exception.toString)
  }
}
