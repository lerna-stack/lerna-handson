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

  /** ConcertActor からのレスポンスメッセージ
    * シリアライズされる
    */
  sealed trait ConcertCommandResponse extends KryoSerializable

  // --

  case class Create(numTickets: Int, replyTo: ActorRef[CreateConcertResponse]) extends Command
  sealed trait CreateConcertResponse                                           extends ConcertCommandResponse
  case class CreateConcertSucceeded(numTickets: Int)                           extends CreateConcertResponse
  case class CreateConcertFailed(error: ConcertError)                          extends CreateConcertResponse

  // --

  case class Get(replyTo: ActorRef[GetConcertResponse])                                extends Command
  sealed trait GetConcertResponse                                                      extends ConcertCommandResponse
  case class GetConcertSucceeded(tickets: Vector[ConcertTicketId], cancelled: Boolean) extends GetConcertResponse
  case class GetConcertFailed(error: ConcertError)                                     extends GetConcertResponse

  // --

  case class Cancel(replyTo: ActorRef[CancelConcertResponse]) extends Command
  sealed trait CancelConcertResponse                          extends ConcertCommandResponse
  case class CancelConcertSucceeded(numberOfTickets: Int)     extends CancelConcertResponse
  case class CancelConcertFailed(error: ConcertError)         extends CancelConcertResponse

  // --

  case class BuyTickets(numberOfTickets: Int, replyTo: ActorRef[BuyConcertTicketsResponse]) extends Command
  sealed trait BuyConcertTicketsResponse                                                    extends ConcertCommandResponse
  case class BuyConcertTicketsSucceeded(tickets: Vector[ConcertTicketId])                   extends BuyConcertTicketsResponse
  case class BuyConcertTicketsFailed(error: ConcertError)                                   extends BuyConcertTicketsResponse
}
