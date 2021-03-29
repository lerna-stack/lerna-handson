package example.usecase

import example.model.concert.ConcertId

import scala.concurrent.Future

/** BoxOffice のユースケースを定義する。
  */
trait BoxOfficeUseCase {
  import BoxOfficeUseCaseProtocol._

  /** コンサートを作製する。
    * @param id コンサートID
    * @param numberOfTickets チケット枚数
    * @return コンサート作成完了情報
    */
  def createConcert(id: ConcertId, numberOfTickets: Int): Future[CreateConcertResponse]

  /** コンサートを取得する。
    * @param id　コンサートID
    * @return コンサート情報
    */
  def getConcert(id: ConcertId): Future[GetConcertResponse]

  /** コンサートをキャンセルする。
    * @param id コンサートID
    * @return コンサートキャンセル完了情報
    */
  def cancelConcert(id: ConcertId): Future[CancelConcertResponse]

  /** コンサートのチケットを購入する。
    * @param id コンサートID
    * @param numberOfTickets 購入チケット枚数
    * @return コンサートチケット購入完了情報
    */
  def buyConcertTickets(id: ConcertId, numberOfTickets: Int): Future[BuyConcertTicketsResponse]
}
