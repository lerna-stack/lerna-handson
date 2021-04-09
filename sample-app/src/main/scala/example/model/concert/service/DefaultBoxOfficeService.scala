package example.model.concert.service

import akka.actor.typed.ActorSystem
import akka.util.Timeout
import example.model.concert.ConcertId
import example.model.concert.actor.{ ConcertActorBehaviorFactory, ConcertActorClusterSharding }

import scala.concurrent.Future

final class DefaultBoxOfficeService(
    system: ActorSystem[Nothing],
    behaviorFactory: ConcertActorBehaviorFactory,
) extends BoxOfficeService {
  import example.model.concert.actor.ConcertActor._

  // 設定を読み込む
  private val config                            = BoxOfficeServiceConfig(system)
  private implicit val responseTimeout: Timeout = config.responseTimeout

  private val sharding = new ConcertActorClusterSharding(system, behaviorFactory)

  override def createConcert(id: ConcertId, numberOfTickets: Int): Future[CreateConcertResponse] = {
    val entityRef = sharding.entityRefFor(id)
    entityRef.ask(replyTo => CreateConcertRequest(numberOfTickets, replyTo))
  }

  override def getConcert(id: ConcertId): Future[GetConcertResponse] = {
    // Ask先から一定時間返答がない場合に再送処理が行われる
    // 永続化等はしていないので、Ask元がクラッシュした場合にはリクエストは失われることに注意すること
    // リクエストが複数回　Ask先に到達して処理される可能性があるので、冪等な処理にのみ使える。
    // TODO Use AtLeastOnceDelivery
    val entityRef = sharding.entityRefFor(id)
    entityRef.ask(replyTo => GetConcertRequest(replyTo))
  }

  override def cancelConcert(id: ConcertId): Future[CancelConcertResponse] = {
    val entityRef = sharding.entityRefFor(id)
    entityRef.ask(replyTo => CancelConcertRequest(replyTo))
  }

  override def buyConcertTickets(id: ConcertId, numberOfTickets: Int): Future[BuyConcertTicketsResponse] = {
    val entityRef = sharding.entityRefFor(id)
    entityRef.ask(replyTo => BuyConcertTicketsRequest(numberOfTickets, replyTo))
  }

}
