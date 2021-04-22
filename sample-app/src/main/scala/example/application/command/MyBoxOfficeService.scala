package example.application.command

import akka.actor.typed.ActorSystem
import akka.util.Timeout
import example.adapter.ConcertId
import example.adapter.command.BoxOfficeService
import example.application.command.actor.{ ConcertActor, ConcertActorBehaviorFactory, ConcertActorClusterSharding }

import scala.concurrent.{ ExecutionContext, Future }

/** 演習用コード
  * Actor とのリクエスト/レスポンスを扱う。
  */
final class MyBoxOfficeService(
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
    ???
  }

  override def getConcert(id: ConcertId): Future[GetConcertResponse] = {
    ???
  }

  override def cancelConcert(id: ConcertId): Future[CancelConcertResponse] = {
    ???
  }

  override def buyConcertTickets(
      id: ConcertId,
      numberOfTickets: Int,
  ): Future[BuyConcertTicketsResponse] = {
    ???
  }

}
