package example.model.concert.actor

import akka.actor.{ ActorSystem, Props, ReceiveTimeout }
import akka.cluster.sharding.ShardRegion.{ HashCodeMessageExtractor, Passivate }
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings }
import example.model.concert._
import example.model.concert.actor.ConcertActorBase.idFor
import jp.co.tis.lerna.util.AtLeastOnceDelivery.AtLeastOnceDeliveryRequest

import scala.concurrent.duration._

object ConcertActorBase {

  /** ShardedConcertActor の ClusterSharding を開始する。
    */
  def startClusterSharding(
      system: ActorSystem,
      config: ConcertActorConfig,
      props: Props,
  ): ConcertActorClusterSharding = {
    new ConcertActorClusterSharding(system, config, props)
  }

  /** ConcertActor の ClusterSharding の情報を保持するクラス
    */
  final class ConcertActorClusterSharding(system: ActorSystem, config: ConcertActorConfig, props: Props) {

    /** ShardRegion を返す
      */
    val shardRegion = ClusterSharding(system).start(
      config.shardName,
      props,
      ClusterShardingSettings(system),
      new MessageExtractor(config.shardCount),
    )
  }

  /** メッセージからシャード名,エンティティIDを抽出するクラス
    * entityId のハッシュコードに基づいてシャーディングされる。
    * @param shardCount シャード数
    */
  private final class MessageExtractor(shardCount: Int) extends HashCodeMessageExtractor(shardCount) {
    override def entityId(message: Any): String = message match {
      case command: ConcertActorProtocol.ConcertCommandRequest =>
        command.concertId.value
      case AtLeastOnceDeliveryRequest(message: ConcertActorProtocol.ConcertCommandRequest) =>
        message.concertId.value
    }
  }

  /** ActorName に対応する ID を取得する。
    * @param actorName
    * @return ID
    */
  def idFor(actorName: String): Either[ConcertError, ConcertId] = {
    ConcertId.fromString(actorName)
  }

  /** ID に対応する ActorName を取得する。
    * @param id
    * @return ActorName
    */
  def actorNameFor(id: ConcertId): String = id.value
}

abstract class ConcertActorBase[State <: ActorStateBase[ConcertEvent, State]]
    extends EventSourcedActorBase[ConcertEvent, State, ConcertStateData] {
  import ConcertActorBaseProtocol._

  /** グローバルで一意なID
    */
  protected val id: ConcertId = {
    // path を ID として復元できない場合は、アプリケーションが不正な状態になっている。
    idFor(self.path.name).left.map(error => new IllegalStateException(error.toString)).toTry.get
  }

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
