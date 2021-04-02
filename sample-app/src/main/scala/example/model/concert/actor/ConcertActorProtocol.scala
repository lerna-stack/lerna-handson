package example.model.concert.actor

import akka.actor.typed.ActorRef
import example.model.KryoSerializable
import example.model.concert._

/** ConcertActor の入出力を定義する
  */
object ConcertActorProtocol {

  /** ConcertActor へのリクエストメッセージ
    * シリアライズされる
    */
  sealed trait ConcertCommandRequest extends KryoSerializable {
    def concertId: ConcertId
  }

  /** ConcertActor からのレスポンスメッセージ
    * シリアライズされる
    */
  sealed trait ConcertCommandResponse extends KryoSerializable

  // --

  case class CreateConcertRequest(concertId: ConcertId, numTickets: Int)(val replyTo: ActorRef[CreateConcertResponse])
      extends ConcertCommandRequest
  sealed trait CreateConcertResponse                  extends ConcertCommandResponse
  case class CreateConcertSucceeded(numTickets: Int)  extends CreateConcertResponse
  case class CreateConcertFailed(error: ConcertError) extends CreateConcertResponse

  // --

  case class GetConcertRequest(concertId: ConcertId)(val replyTo: ActorRef[GetConcertResponse])
      extends ConcertCommandRequest
  sealed trait GetConcertResponse extends ConcertCommandResponse
  case class GetConcertSucceeded(id: ConcertId, tickets: Vector[ConcertTicketId], cancelled: Boolean)
      extends GetConcertResponse
  case class GetConcertFailed(error: ConcertError) extends GetConcertResponse

  // --

  case class CancelConcertRequest(concertId: ConcertId)(val replyTo: ActorRef[CancelConcertResponse])
      extends ConcertCommandRequest
  sealed trait CancelConcertResponse                      extends ConcertCommandResponse
  case class CancelConcertSucceeded(numberOfTickets: Int) extends CancelConcertResponse
  case class CancelConcertFailed(error: ConcertError)     extends CancelConcertResponse

  // --

  case class BuyConcertTicketsRequest(concertId: ConcertId, numberOfTickets: Int)(
      val replyTo: ActorRef[BuyConcertTicketsResponse],
  )                                                                       extends ConcertCommandRequest
  sealed trait BuyConcertTicketsResponse                                  extends ConcertCommandResponse
  case class BuyConcertTicketsSucceeded(tickets: Vector[ConcertTicketId]) extends BuyConcertTicketsResponse
  case class BuyConcertTicketsFailed(error: ConcertError)                 extends BuyConcertTicketsResponse
}
