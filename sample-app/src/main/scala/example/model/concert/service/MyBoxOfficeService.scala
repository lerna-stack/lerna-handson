package example.model.concert.service

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout

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

  override def createConcert(request: CreateConcertRequest): Future[CreateConcertResponse] = {
    ???
  }

  override def getConcert(request: GetConcertRequest): Future[GetConcertResponse] = {
    ???
  }

  override def cancelConcert(request: CancelConcertRequest): Future[CancelConcertResponse] = {
    ???
  }

  override def buyConcertTickets(request: BuyConcertTicketsRequest): Future[BuyConcertTicketsResponse] = {
    ???
  }
}
