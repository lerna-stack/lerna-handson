package example.model.concert.service

import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.{ ActorRef, ActorSystem }
import akka.util.Timeout
import example.model.concert.ConcertId
import example.model.concert.actor.ConcertActorClusterShardingFactory

import scala.concurrent.Future

/** 演習用コード
  * Actor とのリクエスト/レスポンスを扱う。
  */
final class MyBoxOfficeService(
    system: ActorSystem[Nothing],
    factory: ConcertActorClusterShardingFactory,
) extends BoxOfficeService {
  import example.model.concert.actor.ConcertActorProtocol._

  // 設定を読み込む
  private val config                            = BoxOfficeServiceConfig(system)
  private implicit val responseTimeout: Timeout = config.responseTimeout

  private val sharding = factory.create(system)

  override def createConcert(id: ConcertId, numberOfTickets: Int): Future[CreateConcertResponse] = {
    ???
  }

  override def getConcert(id: ConcertId): Future[GetConcertResponse] = {
    ???
  }

  override def cancelConcert(id: ConcertId): Future[CancelConcertResponse] = {
    ???
  }

  override def buyConcertTickets(id: ConcertId, numberOfTickets: Int): Future[BuyConcertTicketsResponse] = {
    ???
  }
}
