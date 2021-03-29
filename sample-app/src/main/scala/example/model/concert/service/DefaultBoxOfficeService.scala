package example.model.concert.service

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future
import jp.co.tis.lerna.util.AtLeastOnceDelivery

final class DefaultBoxOfficeService(
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
    // shardRegionへ問い合わせ
    val anyResponseFuture: Future[Any] = shardRegion.ask(request)
    // 結果を適切な型にキャストする
    val createConcertResponseFuture: Future[CreateConcertResponse] =
      anyResponseFuture.mapTo[CreateConcertResponse]
    createConcertResponseFuture
  }

  override def getConcert(request: GetConcertRequest): Future[GetConcertResponse] = {
    // Ask先から一定時間返答がない場合に再送処理が行われる
    // 永続化等はしていないので、Ask元がクラッシュした場合にはリクエストは失われることに注意すること
    // リクエストが複数回　Ask先に到達して処理される可能性があるので、冪等な処理にのみ使える。
    val anyResponseFuture: Future[Any] =
      AtLeastOnceDelivery.askTo(shardRegion, request)
    val getConcertResponseFuture: Future[GetConcertResponse] =
      anyResponseFuture.mapTo[GetConcertResponse]
    getConcertResponseFuture
  }

  override def cancelConcert(request: CancelConcertRequest): Future[CancelConcertResponse] = {
    shardRegion
      .ask(request)
      .mapTo[CancelConcertResponse]
  }

  override def buyConcertTickets(request: BuyConcertTicketsRequest): Future[BuyConcertTicketsResponse] = {
    shardRegion
      .ask(request)
      .mapTo[BuyConcertTicketsResponse]
  }
}
