package example.usecase

import example.model.concert.actor.ConcertActor
import example.model.concert.ConcertId
import example.model.concert.service.BoxOfficeService

import scala.concurrent._

object DefaultBoxOfficeUseCase {
  type UseCaseExecutionContext = ExecutionContext
}
final class DefaultBoxOfficeUseCase(
    boxOfficeService: BoxOfficeService,
)(implicit
    executionContext: DefaultBoxOfficeUseCase.UseCaseExecutionContext,
) extends BoxOfficeUseCase {
  import BoxOfficeUseCaseProtocol._

  override def createConcert(id: ConcertId, numberOfTickets: Int): Future[CreateConcertResponse] = {
    boxOfficeService
      .createConcert(id, numberOfTickets)
      .map {
        case succeeded: ConcertActor.CreateSucceeded =>
          CreateConcertResponse(succeeded.numTickets)
        case failed: ConcertActor.CreateFailed =>
          throw new BoxOfficeUseCaseException(failed.error)
      }
  }

  override def getConcert(id: ConcertId): Future[GetConcertResponse] = {
    boxOfficeService
      .getConcert(id)
      .map {
        case succeeded: ConcertActor.GetSucceeded =>
          GetConcertResponse(succeeded.tickets, succeeded.cancelled)
        case failed: ConcertActor.GetFailed =>
          throw new BoxOfficeUseCaseException(failed.error)
      }
  }

  override def cancelConcert(id: ConcertId): Future[CancelConcertResponse] = {
    boxOfficeService
      .cancelConcert(id)
      .map {
        case succeeded: ConcertActor.CancelSucceeded =>
          CancelConcertResponse(succeeded.numberOfTickets)
        case failed: ConcertActor.CancelFailed =>
          throw new BoxOfficeUseCaseException(failed.error)
      }
  }

  override def buyConcertTickets(id: ConcertId, numberOfTickets: Int): Future[BuyConcertTicketsResponse] = {
    boxOfficeService
      .buyConcertTickets(id, numberOfTickets)
      .map {
        case succeeded: ConcertActor.BuyTicketsSucceeded =>
          BuyConcertTicketsResponse(succeeded.tickets)
        case failed: ConcertActor.BuyTicketsFailed =>
          throw new BoxOfficeUseCaseException(failed.error)
      }
  }
}
