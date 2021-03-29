package example.readmodel

import example.model.concert.ConcertId

/** コンサート一覧項目
  * @param id コンサートID
  * @param numberOfTickets 残りチケット枚数
  * @param cancelled キャンセル済みか
  */
case class ConcertItem(id: ConcertId, numberOfTickets: Int, cancelled: Boolean)
