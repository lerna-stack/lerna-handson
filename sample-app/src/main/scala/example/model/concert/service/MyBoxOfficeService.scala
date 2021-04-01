package example.model.concert.service

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import example.model.concert.ConcertId

import scala.concurrent.Future
import jp.co.tis.lerna.util.AtLeastOnceDelivery
import jp.co.tis.lerna.util.AtLeastOnceDelivery._

/** 演習用コード
  * Actor とのリクエスト/レスポンスを扱う。
  */
final class MyBoxOfficeService(
    system: ActorSystem,
    factory: ConcertActorClusterShardingFactory,
) extends BoxOfficeService {
  import example.model.concert.actor.ConcertActorProtocol._

  private implicit val systemForAskTo: ActorSystem = system

  // 設定を読み込む
  private val config                            = BoxOfficeServiceConfig(system)
  private implicit val responseTimeout: Timeout = config.responseTimeout

  // 起動時に ShardedConcertActor の ClusterSharding を開始する。
  private val shardRegion = factory.create(system).shardRegion

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
