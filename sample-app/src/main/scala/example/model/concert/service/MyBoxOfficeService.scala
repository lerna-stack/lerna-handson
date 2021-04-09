package example.model.concert.service

import akka.actor.typed.ActorSystem
import akka.util.Timeout
import example.model.concert.ConcertId
import example.model.concert.actor.{ ConcertActor, ConcertActorBehaviorFactory, ConcertActorClusterSharding }

import scala.concurrent.Future

/** 演習用コード
  * Actor とのリクエスト/レスポンスを扱う。
  */
final class MyBoxOfficeService(
    system: ActorSystem[Nothing],
    behaviorFactory: ConcertActorBehaviorFactory,
) extends BoxOfficeService {

  // 設定を読み込む
  private val config                            = BoxOfficeServiceConfig(system)
  private implicit val responseTimeout: Timeout = config.responseTimeout

  private val sharding = new ConcertActorClusterSharding(system, behaviorFactory)

  override def createConcert(id: ConcertId, numberOfTickets: Int): Future[ConcertActor.CreateResponse] = {
    ???
  }

  override def getConcert(id: ConcertId): Future[ConcertActor.GetResponse] = {
    ???
  }

  override def cancelConcert(id: ConcertId): Future[ConcertActor.CancelResponse] = {
    ???
  }

  override def buyConcertTickets(id: ConcertId, numberOfTickets: Int): Future[ConcertActor.BuyTicketsResponse] = {
    ???
  }
}
