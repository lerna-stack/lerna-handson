package example.application.http

import akka.Done
import akka.actor.{ ActorSystem, CoordinatedShutdown }
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import org.slf4j.LoggerFactory

import scala.concurrent._

final class MainHttpApiServer(
    config: MainHttpApiServerConfig,
    resource: MainHttpApiServerResource,
)(implicit
    system: ActorSystem,
) {
  import system.dispatcher

  private val log = LoggerFactory.getLogger(this.getClass)

  def start(): Future[Done] = {
    // HTTPサーバーの起動
    Http()
      .bindAndHandle(resource.routes, config.host, config.port)
      .map(onBindSuccess)
      .map(_ => Done)
  }

  private def onBindSuccess(binding: ServerBinding): Unit = {
    // 起動に成功した場合、終了処理を登録する。
    log.info("Success to bind to {}", binding.localAddress)
    CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseServiceUnbind, "api-server-http-unbind") { () =>
      // HTTPリクエストの受付を停止する。
      log.info("[Shutdown] HTTPリクエストの受付を停止します {}", binding)
      binding.unbind().map { _ =>
        log.info("[Shutdown] HTTPリクエストの受付を停止しました {}", binding)
        Done
      }
    }
    CoordinatedShutdown(system).addTask(
      CoordinatedShutdown.PhaseBeforeActorSystemTerminate,
      "api-server-http-terminate",
    ) { () =>
      // HTTP サーバを停止する。
      log.info("[Shutdown] HTTPサーバを停止します {}", binding)
      binding.terminate(config.terminationHardDeadline).map { _ =>
        log.info("[Shutdown] HTTPサーバを停止しました {}", binding)
        Done
      }
    }
  }
}
