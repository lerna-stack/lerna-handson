package example.usecase

import example.model.concert._

/** BoxOfficeUseCase の入出力プロトコルを定義する。
  */
object BoxOfficeUseCaseProtocol {

  /** コンサート作成完了
    * @param id
    * @param numberOfTickets
    */
  final case class CreateConcertResponse(numberOfTickets: Int)

  /** コンサート取得完了
    * @param id コンサートID
    * @param concert コンサート情報
    */
  final case class GetConcertResponse(tickets: Vector[ConcertTicketId], cancelled: Boolean)

  /** コンサートキャンセル完了
    * @param id コンサートID
    * @param numberOfTickets 残りチケット枚数
    */
  final case class CancelConcertResponse(numberOfTickets: Int)

  /** コンサートチケット購入完了
    * @param id コンサートID
    * @param tickets 購入チケット
    */
  final case class BuyConcertTicketsResponse(tickets: Vector[ConcertTicketId])
}
