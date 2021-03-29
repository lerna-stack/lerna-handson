package example.model.concert.actor

import example.model.KryoSerializable

/** ConcertActorBase への入出力を定義する
  */
object ConcertActorBaseProtocol {

  /** ConcertActorBase へのリクエストメッセージ。
    * シリアライズされる。
    */
  sealed trait ConcertActorBaseRequest extends KryoSerializable

  /** ConcertActorBase　へのレスポンスメッセージ。
    * シリアライズされる。
    */
  sealed trait ConcertActorBaseResponse extends KryoSerializable

  //--

  /** パッシベーション
    */
  case object StopRequest extends ConcertActorBaseRequest

}
