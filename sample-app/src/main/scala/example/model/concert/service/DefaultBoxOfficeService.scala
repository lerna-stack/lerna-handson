package example.model.concert.service

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.{ ActorRef, ActorSystem }
import akka.{ actor => classic }
import akka.util.Timeout
import example.model.concert.ConcertId

import scala.concurrent.Future

final class DefaultBoxOfficeService(
    system: classic.ActorSystem,
    factory: ConcertActorClusterShardingFactory,
) extends BoxOfficeService {
  import example.model.concert.actor.ConcertActorProtocol._

  private implicit val systemForAskTo: ActorSystem[Nothing] = system.toTyped

  // 設定を読み込む
  private val config                            = BoxOfficeServiceConfig(system)
  private implicit val responseTimeout: Timeout = config.responseTimeout

  // 起動時に ShardedConcertActor の ClusterSharding を開始する。
  private val shardRegion: ActorRef[ConcertCommandRequest] = factory.create(system).shardRegion

  override def createConcert(id: ConcertId, numberOfTickets: Int): Future[CreateConcertResponse] = {
    // shardRegionへ問い合わせ
    shardRegion ? CreateConcertRequest(id, numberOfTickets)
  }

  override def getConcert(id: ConcertId): Future[GetConcertResponse] = {
    // Ask先から一定時間返答がない場合に再送処理が行われる
    // 永続化等はしていないので、Ask元がクラッシュした場合にはリクエストは失われることに注意すること
    // リクエストが複数回　Ask先に到達して処理される可能性があるので、冪等な処理にのみ使える。
    // TODO Use AtLeastOnceDelivery
    shardRegion ? GetConcertRequest(id)
  }

  override def cancelConcert(id: ConcertId): Future[CancelConcertResponse] = {
    shardRegion ? CancelConcertRequest(id)
  }

  override def buyConcertTickets(id: ConcertId, numberOfTickets: Int): Future[BuyConcertTicketsResponse] = {
    shardRegion ? BuyConcertTicketsRequest(id, numberOfTickets)
  }

}
