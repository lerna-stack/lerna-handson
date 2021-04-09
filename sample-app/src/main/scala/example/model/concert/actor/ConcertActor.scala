package example.model.concert.actor

import akka.actor.typed.ActorRef
import example.model.KryoSerializable
import example.model.concert._

/** このサンプルアプリでは、テストコードの共通化などを目的として、
  * 次の3つの Behavior のプロトコルを [[ConcertActor]] にて共通で定義している。
  *  - [[DefaultConcertActor]]
  *  - [[MyConcertActor]]
  *  - [[DefaultConcertActorWithEventPersistence]]
  *
  * この方法は一般的な方法ではなく、特殊な方法になっていることに注意すること。
  *
  * 一般的な方法に従うと、Behavior の定義と一緒に入力プロトコルを定義することになる。
  * 例えば次のようになる。
  *   - `DefaultConcertActor.Command`
  *   - `MyConcertActor.Command`
  *   - `DefaultConcertActorWithEventPersistence.Command`
  */
object ConcertActor {

  /** ConcertActor へのリクエスト(コマンド)
    *
    * シリアライズされる
    */
  sealed trait Command extends KryoSerializable

  /** ConcertActor からのレスポンス
    *
    * シリアライズされる
    */
  sealed trait Response extends KryoSerializable

  // --

  final case class Create(numTickets: Int, replyTo: ActorRef[CreateResponse]) extends Command
  sealed trait CreateResponse                                                 extends Response
  final case class CreateSucceeded(numTickets: Int)                           extends CreateResponse
  final case class CreateFailed(error: ConcertError)                          extends CreateResponse

  // --

  final case class Get(replyTo: ActorRef[GetResponse])                                extends Command
  sealed trait GetResponse                                                            extends Response
  final case class GetSucceeded(tickets: Vector[ConcertTicketId], cancelled: Boolean) extends GetResponse
  final case class GetFailed(error: ConcertError)                                     extends GetResponse

  // --

  final case class Cancel(replyTo: ActorRef[CancelResponse]) extends Command
  sealed trait CancelResponse                                extends Response
  final case class CancelSucceeded(numberOfTickets: Int)     extends CancelResponse
  final case class CancelFailed(error: ConcertError)         extends CancelResponse

  // --

  final case class BuyTickets(numberOfTickets: Int, replyTo: ActorRef[BuyTicketsResponse]) extends Command
  sealed trait BuyTicketsResponse                                                          extends Response
  final case class BuyTicketsSucceeded(tickets: Vector[ConcertTicketId])                   extends BuyTicketsResponse
  final case class BuyTicketsFailed(error: ConcertError)                                   extends BuyTicketsResponse

}
