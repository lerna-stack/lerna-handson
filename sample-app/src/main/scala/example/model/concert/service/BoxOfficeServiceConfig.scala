package example.model.concert.service

import akka.actor.typed.ActorSystem
import akka.util.Timeout

import scala.concurrent.duration._

object BoxOfficeServiceConfig {
  def apply(system: ActorSystem[Nothing]): BoxOfficeServiceConfig = {
    val config = system.settings.config.getConfig("example.box-office-service")
    new BoxOfficeServiceConfig(
      config.getDuration("response-timeout", MICROSECONDS).micros,
    )
  }
}

/** BoxOfficeService の設定
  */
final class BoxOfficeServiceConfig(
    /** Actor からのレスポンスタイムアウト
      */
    val responseTimeout: Timeout,
)
