package example.usecase

import example.readmodel.ConcertRepository

import scala.concurrent._

object DefaultBoxOfficeReadModelUseCase {
  type UseCaseExecutionContext = ExecutionContext
}
final class DefaultBoxOfficeReadModelUseCase(
    repository: ConcertRepository,
)(implicit
    executionContext: DefaultBoxOfficeReadModelUseCase.UseCaseExecutionContext,
) extends BoxOfficeReadModelUseCase {
  import BoxOfficeReadModelUseCaseProtocol._

  /** コンサート一覧を取得する。
    *
    * @return コンサート一覧
    */
  override def getConcertList(excludeCancelled: Boolean): Future[GetConcertListResponse] = {
    repository
      .fetchConcertList(excludeCancelled)
      .map(repositoryItems => {
        val items = repositoryItems.map(item => {
          GetConcertItemResponse(item.id, item.numberOfTickets, item.cancelled)
        })
        GetConcertListResponse(items)
      })
  }
}
