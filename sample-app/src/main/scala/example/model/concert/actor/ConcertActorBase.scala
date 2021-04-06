package example.model.concert.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior, Terminated }
import akka.actor.{ Props, ReceiveTimeout }
import akka.cluster.sharding.ShardRegion.Passivate
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.scaladsl.{ ClusterSharding, Entity, EntityRef, EntityTypeKey }
import example.model.concert._
import example.model.concert.actor.ConcertActorProtocol.ConcertCommandRequest
import jp.co.tis.lerna.util.AtLeastOnceDelivery.AtLeastOnceDeliveryRequest

import scala.concurrent.duration._

object ConcertActorBase {

  /** ShardedConcertActor の ClusterSharding を開始する。
    */
  def startClusterSharding(
      system: ActorSystem[Nothing],
      config: ConcertActorConfig,
      createBehavior: ConcertActorBehaviorFactory,
  ): ConcertActorClusterSharding = {
    new ConcertActorClusterSharding(system, config, createBehavior)
  }

  /** ConcertActor の ClusterSharding の情報を保持するクラス
    */
  final class ConcertActorClusterSharding(
      system: ActorSystem[Nothing],
      config: ConcertActorConfig,
      createBehavior: ConcertActorBehaviorFactory,
  ) {
    private val sharding                                      = ClusterSharding(system)
    private val TypeKey: EntityTypeKey[ConcertCommandRequest] = EntityTypeKey[ConcertCommandRequest](config.shardName)

    /** ShardRegion を返す
      */
    val shardRegion: ActorRef[ShardingEnvelope[ConcertCommandRequest]] =
      sharding.init(Entity(TypeKey) { entityContext =>
        val id = ConcertId
          .fromString(entityContext.entityId)
          .left.map(error => new IllegalStateException(error.toString))
          .toTry.get
        createBehavior(id)
      })

    def entityRefFor(id: ConcertId): EntityRef[ConcertCommandRequest] = {
      sharding.entityRefFor(TypeKey, id.value)
    }

  }

  // [暫定] 外部からは Typed に見えるようにする
  def createBehavior(props: Props): Behavior[ConcertCommandRequest] = {
    Behaviors.setup { context =>
      val classic = context.actorOf(props)
      context.watch(classic)
      Behaviors
        .receiveMessage[ConcertCommandRequest] { request =>
          classic.tell(request, context.self.toClassic)
          Behaviors.same
        }.receiveSignal {
          case (_, Terminated(value)) =>
            Behaviors.stopped
        }
    }
  }

}

abstract class ConcertActorBase[State <: ActorStateBase[ConcertEvent, State]]
    extends EventSourcedActorBase[ConcertEvent, State, ConcertStateData] {
  import ConcertActorBaseProtocol._

  // 初期値では passivate しないようにしておく (演習を通してサブクラスでオーバライドする)
  protected def passivateTimeout: Duration = Duration.Undefined

  // メッセージを一定期間受信できない場合はリソース節約のためPassivate処理を起動するためタイマーを設定する
  // タイムアウトの値は、フィールドで設定することにした(設定ファイルでやるほうが多い)
  context.setReceiveTimeout(passivateTimeout)

  override def receiveCommand: Receive = {
    handlePassivation orElse
    handleAtLeastOnceDeliveryRequest orElse
    super.receiveCommand
  }

  protected def handlePassivation: Receive = {
    case ReceiveTimeout =>
      log.info("Passivate {}", self)
      context.parent ! Passivate(stopMessage = StopRequest)
    case StopRequest =>
      log.info("Stop {} by Passivation", self)
      context.stop(self)
  }

  protected def handleAtLeastOnceDeliveryRequest: Receive = {
    case request: AtLeastOnceDeliveryRequest if super.receiveCommand.isDefinedAt(request.originalMessage) =>
      super.receiveCommand(request.originalMessage)
      // persist() の処理がある場合に完了してから accept する
      defer(()) { _ =>
        request.accept()
      }
  }
}
