package example.model.concert.service

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.{ ActorRef, ActorSystem }
import akka.{ actor => classic }
import akka.util.Timeout
import example.model.concert.ConcertId

import scala.concurrent.Future

/** 演習用コード
  * Actor とのリクエスト/レスポンスを扱う。
  */
final class MyBoxOfficeService(
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
