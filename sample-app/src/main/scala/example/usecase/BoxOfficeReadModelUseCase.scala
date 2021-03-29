package example.usecase

import scala.concurrent.Future

/** BoxOffice の ReadModel ユースケース
  */
trait BoxOfficeReadModelUseCase {
  import BoxOfficeReadModelUseCaseProtocol._

  /** コンサート一覧を取得する。
    * @param excludeCancelled キャンセル済みを除外する
    * @return コンサート一覧
    */
  def getConcertList(excludeCancelled: Boolean): Future[GetConcertListResponse]
}
