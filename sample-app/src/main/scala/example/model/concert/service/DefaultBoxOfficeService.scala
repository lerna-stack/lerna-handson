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

  override def createConcert(id: ConcertId, numberOfTickets: Int): Future[CreateResponse] = {
    val entityRef = sharding.entityRefFor(id)
    entityRef.ask(replyTo => Create(numberOfTickets, replyTo))
  }

  override def getConcert(id: ConcertId): Future[GetResponse] = {
    // Ask先から一定時間返答がない場合に再送処理が行われる
    // 永続化等はしていないので、Ask元がクラッシュした場合にはリクエストは失われることに注意すること
    // リクエストが複数回　Ask先に到達して処理される可能性があるので、冪等な処理にのみ使える。
    // TODO Use AtLeastOnceDelivery
    val entityRef = sharding.entityRefFor(id)
    entityRef.ask(replyTo => Get(replyTo))
  }

  override def cancelConcert(id: ConcertId): Future[CancelResponse] = {
    val entityRef = sharding.entityRefFor(id)
    entityRef.ask(replyTo => Cancel(replyTo))
  }

  override def buyConcertTickets(id: ConcertId, numberOfTickets: Int): Future[BuyTicketsResponse] = {
    val entityRef = sharding.entityRefFor(id)
    entityRef.ask(replyTo => BuyTickets(numberOfTickets, replyTo))
  }

}
