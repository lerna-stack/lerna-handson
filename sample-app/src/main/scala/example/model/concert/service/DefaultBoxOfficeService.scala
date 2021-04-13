package example.model.concert.service

import akka.actor.typed.ActorSystem
import akka.util.Timeout
import example.model.concert.ConcertId
import example.model.concert.actor.{ ConcertActor, ConcertActorBehaviorFactory, ConcertActorClusterSharding }

import scala.concurrent.{ ExecutionContext, Future }

final class DefaultBoxOfficeService(
    system: ActorSystem[Nothing],
    behaviorFactory: ConcertActorBehaviorFactory,
) extends BoxOfficeService {
  import BoxOfficeService._

  // 設定を読み込む
  private val config                            = BoxOfficeServiceConfig(system)
  private implicit val responseTimeout: Timeout = config.responseTimeout

  private implicit val executionContext: ExecutionContext = system.executionContext
  private val sharding                                    = new ConcertActorClusterSharding(system, behaviorFactory)

  override def createConcert(id: ConcertId, numberOfTickets: Int): Future[CreateConcertResponse] = {
    val entityRef = sharding.entityRefFor(id)
    entityRef
      .ask(replyTo => ConcertActor.Create(numberOfTickets, replyTo))
      .flatMap {
        case createConcertSucceeded: ConcertActor.CreateSucceeded =>
          Future.successful {
            CreateConcertResponse(createConcertSucceeded.numTickets)
          }
        case createConcertFailed: ConcertActor.CreateFailed =>
          Future.failed {
            new BoxOfficeServiceException(createConcertFailed.error)
          }
      }
  }

  override def getConcert(id: ConcertId): Future[GetConcertResponse] = {
    val entityRef = sharding.entityRefFor(id)
    entityRef
      .ask(replyTo => ConcertActor.Get(replyTo))
      .flatMap {
        case succeeded: ConcertActor.GetSucceeded =>
          Future.successful {
            GetConcertResponse(succeeded.tickets, succeeded.cancelled)
          }
        case failed: ConcertActor.GetFailed =>
          Future.failed {
            new BoxOfficeServiceException(failed.error)
          }
      }
  }

  override def cancelConcert(id: ConcertId): Future[CancelConcertResponse] = {
    val entityRef = sharding.entityRefFor(id)
    entityRef
      .ask(replyTo => ConcertActor.Cancel(replyTo))
      .flatMap {
        case succeeded: ConcertActor.CancelSucceeded =>
          Future.successful {
            CancelConcertResponse(succeeded.numberOfTickets)
          }
        case failed: ConcertActor.CancelFailed =>
          Future.failed {
            new BoxOfficeServiceException(failed.error)
          }
      }
  }

  override def buyConcertTickets(
      id: ConcertId,
      numberOfTickets: Int,
  ): Future[BuyConcertTicketsResponse] = {
    val entityRef = sharding.entityRefFor(id)
    entityRef
      .ask(replyTo => ConcertActor.BuyTickets(numberOfTickets, replyTo))
      .flatMap {
        case succeeded: ConcertActor.BuyTicketsSucceeded =>
          Future.successful {
            BuyConcertTicketsResponse(succeeded.tickets)
          }
        case failed: ConcertActor.BuyTicketsFailed =>
          Future.failed {
            new BoxOfficeServiceException(failed.error)
          }
      }
  }

}
