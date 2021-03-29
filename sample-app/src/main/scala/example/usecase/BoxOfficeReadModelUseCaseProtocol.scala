package example.usecase

import example.model.concert.ConcertId

/** BoxOfficeReadModelUseCase の入出力プロトコルを定義する。
  */
object BoxOfficeReadModelUseCaseProtocol {

  /** コンサート一覧取得結果
    * @param items コンサート一覧
    */
  final case class GetConcertListResponse(items: Seq[GetConcertItemResponse])

  /** コンサート一覧項目
    * @param id コンサートID
    * @param numberOfTickets 残チケット枚数
    * @param cancelled キャンセル済みか
    */
  final case class GetConcertItemResponse(id: ConcertId, numberOfTickets: Int, cancelled: Boolean)
}
