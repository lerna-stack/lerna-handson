package example.presentation

import akka.event.Logging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ ExceptionHandler, Route }
import example.adapter.ConcertRepository
import example.adapter.command.BoxOfficeService
import example.presentation.protocol._

import scala.concurrent._

/** 演習でルートを実装していくクラス
  */
final class MyBoxOfficeResource(
    service: BoxOfficeService,
    concertRepository: ConcertRepository,
)(implicit
    executionContext: PresentationExecutionContext,
) extends BoxOfficeResource {
  import SprayJsonSupport._
  import example.adapter.command.BoxOfficeService._
  import example.adapter.ConcertRepository._
  import example.presentation.dsl.ConcertPathMatchers._

  // ExceptionHandler を定義する
  // BoxOfficeExceptionHandler で処理できないものは GlobalExceptionHandler で処理する
  private val exceptionHandler: ExceptionHandler =
    BoxOfficeExceptionHandler.handler orElse GlobalExceptionHandler.handler

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
          val serviceResponseFuture: Future[CreateConcertResponse] =
            service.createConcert(concertId, body.tickets)
          val responseFuture: Future[CreateConcertResponseBody] =
            serviceResponseFuture.map(response => {
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
        val serviceResponseFuture: Future[CancelConcertResponse] =
          service.cancelConcert(concertId)
        val responseFuture: Future[CancelConcertResponseBody] =
          serviceResponseFuture.map(response => {
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
          val serviceResponseFuture: Future[BuyConcertTicketsResponse] =
            service.buyConcertTickets(concertId, body.tickets)
          val responseFuture: Future[BuyConcertTicketsResponseBody] =
            serviceResponseFuture.map(response => {
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
