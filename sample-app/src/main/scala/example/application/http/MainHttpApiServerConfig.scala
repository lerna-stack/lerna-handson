package example.application.http

import akka.actor.typed.ActorSystem

import scala.concurrent.duration._

object MainHttpApiServerConfig {
  def apply(system: ActorSystem[Nothing]): MainHttpApiServerConfig = {
    val config = system.settings.config.getConfig("example.http-api-server")
    new MainHttpApiServerConfig(
      config.getString("host"),
      config.getInt("port"),
      config.getDuration("termination-hard-deadline", MICROSECONDS).micro,
    )
  }
}

/** MainHttpApiServer の設定。
  * 22制限があるので、設定で case class は使わないこと
  * @param host
  * @param port
  * @param terminationHardDeadline
  */
final class MainHttpApiServerConfig(
    /** ホスト名
      */
    val host: String,
    /** ポート番号
      */
    val port: Int,
    /** 終了処理タイムアウトのハードリミット
      */
    val terminationHardDeadline: FiniteDuration,
)
