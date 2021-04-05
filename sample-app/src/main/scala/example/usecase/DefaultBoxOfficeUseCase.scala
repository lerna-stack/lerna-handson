package example.usecase

import example.model.concert.actor.ConcertActorProtocol._
import example.model.concert.ConcertId
import example.model.concert.service.BoxOfficeService

import scala.concurrent._

object DefaultBoxOfficeUseCase {
  type UseCaseExecutionContext = ExecutionContext
}
final class DefaultBoxOfficeUseCase(
    boxOfficeService: BoxOfficeService,
)(implicit
    dispatcher: DefaultBoxOfficeUseCase.UseCaseExecutionContext,
) extends BoxOfficeUseCase {
  import BoxOfficeUseCaseProtocol._

  override def createConcert(id: ConcertId, numberOfTickets: Int): Future[CreateConcertResponse] = {
    boxOfficeService
      .createConcert(id, numberOfTickets)
      .map {
        case succeeded: CreateConcertSucceeded =>
          CreateConcertResponse(succeeded.numTickets)
        case failed: CreateConcertFailed =>
          throw new BoxOfficeUseCaseException(failed.error)
      }
  }

  override def getConcert(id: ConcertId): Future[GetConcertResponse] = {
    boxOfficeService
      .getConcert(id)
      .map {
        case succeeded: GetConcertSucceeded =>
          GetConcertResponse(succeeded.tickets, succeeded.cancelled)
        case failed: GetConcertFailed =>
          throw new BoxOfficeUseCaseException(failed.error)
      }
  }

  override def cancelConcert(id: ConcertId): Future[CancelConcertResponse] = {
    boxOfficeService
      .cancelConcert(id)
      .map {
        case succeeded: CancelConcertSucceeded =>
          CancelConcertResponse(succeeded.numberOfTickets)
        case failed: CancelConcertFailed =>
          throw new BoxOfficeUseCaseException(failed.error)
      }
  }

  override def buyConcertTickets(id: ConcertId, numberOfTickets: Int): Future[BuyConcertTicketsResponse] = {
    boxOfficeService
      .buyConcertTickets(id, numberOfTickets)
      .map {
        case succeeded: BuyConcertTicketsSucceeded =>
          BuyConcertTicketsResponse(succeeded.tickets)
        case failed: BuyConcertTicketsFailed =>
          throw new BoxOfficeUseCaseException(failed.error)
      }
  }
}
