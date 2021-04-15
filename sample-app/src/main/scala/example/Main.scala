package example

import akka.Done
import akka.actor.CoordinatedShutdown
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorSystem, Behavior }
import akka.cluster.Cluster
import example.application.http._
import example.readmodel.rdb.projection.ConcertProjection
import org.slf4j.LoggerFactory

import scala.concurrent._
import scala.util.Failure

object Main extends App {
  object Guardian {
    def apply(): Behavior[Nothing] = Behaviors.empty
  }
  val logger          = LoggerFactory.getLogger(this.getClass)
  implicit val system = ActorSystem[Nothing](Guardian(), "concerts")
  import system.executionContext

  // DIデザインを作成する。
  val design = MainDiDesign.design(system)

  // DIセッションを作成する。
  // DIセッションは ActorSystemの終了直前で クローズする。
  val session = design.newSession
  CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseBeforeActorSystemTerminate, "airframe-session-close") {
    () =>
      Future {
        session.close()
        Done
      }
  }

  // Clusterに参加できたらサービスを開始する。
  Cluster(system).registerOnMemberUp {
    startHttpServer()
    startProjection()
  }

  // HTTP Server を起動する。
  // 起動に失敗したら ActorSystem 自体を終了させる。
  private def startHttpServer(): Unit = {
    val server = session.build[MainHttpApiServer]
    server.start().andThen {
      case Failure(error) =>
        logger.error(s"$error")
        system.terminate()
    }
  }

  // Projection を起動する。
  private def startProjection(): Unit = {
    val projection = session.build[ConcertProjection]
    projection.start()
  }

}
