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

  /** ConcertActor へのリクエストメッセージ
    * シリアライズされる
    */
  sealed trait ConcertCommandRequest extends KryoSerializable

  /** ConcertActor からのレスポンスメッセージ
    * シリアライズされる
    */
  sealed trait ConcertCommandResponse extends KryoSerializable

  // --

  case class CreateConcertRequest(numTickets: Int, replyTo: ActorRef[CreateConcertResponse])
      extends ConcertCommandRequest
  sealed trait CreateConcertResponse                  extends ConcertCommandResponse
  case class CreateConcertSucceeded(numTickets: Int)  extends CreateConcertResponse
  case class CreateConcertFailed(error: ConcertError) extends CreateConcertResponse

  // --

  case class GetConcertRequest(replyTo: ActorRef[GetConcertResponse])                  extends ConcertCommandRequest
  sealed trait GetConcertResponse                                                      extends ConcertCommandResponse
  case class GetConcertSucceeded(tickets: Vector[ConcertTicketId], cancelled: Boolean) extends GetConcertResponse
  case class GetConcertFailed(error: ConcertError)                                     extends GetConcertResponse

  // --

  case class CancelConcertRequest(replyTo: ActorRef[CancelConcertResponse]) extends ConcertCommandRequest
  sealed trait CancelConcertResponse                                        extends ConcertCommandResponse
  case class CancelConcertSucceeded(numberOfTickets: Int)                   extends CancelConcertResponse
  case class CancelConcertFailed(error: ConcertError)                       extends CancelConcertResponse

  // --

  case class BuyConcertTicketsRequest(numberOfTickets: Int, replyTo: ActorRef[BuyConcertTicketsResponse])
      extends ConcertCommandRequest
  sealed trait BuyConcertTicketsResponse                                  extends ConcertCommandResponse
  case class BuyConcertTicketsSucceeded(tickets: Vector[ConcertTicketId]) extends BuyConcertTicketsResponse
  case class BuyConcertTicketsFailed(error: ConcertError)                 extends BuyConcertTicketsResponse
}
