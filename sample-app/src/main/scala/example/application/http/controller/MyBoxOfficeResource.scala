package example.application.http.controller

import akka.event.Logging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ ExceptionHandler, Route }
import example.application.http._
import example.application.http.protocol._
import example.model.concert.ConcertId
import example.readmodel.{ ConcertItem, ConcertRepository }
import example.usecase._

import scala.concurrent._

/** 演習でルートを実装していくクラス
  */
final class MyBoxOfficeResource(
    boxOfficeUseCase: BoxOfficeUseCase,
    concertRepository: ConcertRepository,
)(implicit
    executionContext: ResourceExecutionContext,
) extends MainHttpApiServerResource {
  import SprayJsonSupport._
  import example.application.http.dsl.ConcertPathMatchers._
  import example.usecase.BoxOfficeUseCaseProtocol._

  // ExceptionHandler を定義する
  // BoxOfficeExceptionHandler で処理できないものは GlobalExceptionHandler で処理する
  private val exceptionHandler: ExceptionHandler =
    DefaultBoxOfficeExceptionHandler.handler orElse DefaultGlobalExceptionHandler.handler

  // BoxOffice の HTTPルート
  override def routes: Route = {
    logRequestResult(("[HTTP] ", Logging.InfoLevel)) {
      handleExceptions(exceptionHandler) {
        concat(
          concertGetRoute,
          concertCreateRoute,
          concertCancelRoute,
          concertTicketRoute,
          concertSearchRoute,
        )
      }
    }
  }

  // コンサート取得
  // 演習で実装する箇所
  private def concertGetRoute: Route = {
    ???
  }

  // コンサート作成
  private def concertCreateRoute: Route = {
    path("concerts" / ConcertIdentifier) { concertId =>
      post {
        entity(as[CreateConcertRequestBody]) { body =>
          val useCaseCreateResponseFuture: Future[CreateConcertResponse] =
            boxOfficeUseCase.createConcert(concertId, body.tickets)
          val responseFuture: Future[CreateConcertResponseBody] =
            useCaseCreateResponseFuture.map(response => {
              CreateConcertResponseBody.from(concertId, response)
            })
          onSuccess(responseFuture) { response =>
            complete(StatusCodes.OK -> response)
          }
        }
      }
    }
  }

  // コンサートキャンセル
  private def concertCancelRoute: Route = {
    path("concerts" / ConcertIdentifier / "cancel") { concertId =>
      post {
        val useCaseCancelResponseFuture: Future[CancelConcertResponse] =
          boxOfficeUseCase.cancelConcert(concertId)
        val responseFuture: Future[CancelConcertResponseBody] =
          useCaseCancelResponseFuture.map(response => {
            CancelConcertResponseBody.from(concertId, response)
          })
        onSuccess(responseFuture) { response =>
          complete(StatusCodes.OK -> response)
        }
      }
    }
  }

  // コンサートチケット購入
  private def concertTicketRoute: Route = {
    path("concerts" / ConcertIdentifier / "tickets") { concertId =>
      post {
        entity(as[BuyConcertTicketsRequestBody]) { body =>
          val useCaseBuyResponseFuture: Future[BuyConcertTicketsResponse] =
            boxOfficeUseCase.buyConcertTickets(concertId, body.tickets)
          val responseFuture: Future[BuyConcertTicketsResponseBody] =
            useCaseBuyResponseFuture.map(response => {
              BuyConcertTicketsResponseBody.from(concertId, response)
            })
          onSuccess(responseFuture) { response =>
            complete(StatusCodes.OK -> response)
          }
        }
      }
    }
  }

  // コンサート一覧
  private def concertSearchRoute: Route = {
    path("concerts") {
      get {
        parameters("excludeCancelled".as[Boolean] ? false) { excludeCancelled =>
          val repositoryResponseFuture: Future[Seq[ConcertItem]] =
            concertRepository.fetchConcertList(excludeCancelled)
          val responseFuture: Future[GetConcertsResponseBody] =
            repositoryResponseFuture.map(GetConcertsResponseBody.from)
          onSuccess(responseFuture) { response =>
            complete(StatusCodes.OK -> response)
          }
        }
      }
    }
  }
}
