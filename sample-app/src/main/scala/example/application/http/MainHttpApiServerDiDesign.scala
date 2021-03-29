package example.application.http

import akka.actor.ActorSystem
import example.application.http.controller._
import wvlet.airframe.Design

object MainHttpApiServerDiDesign {
  // format: off
  lazy val design: Design =
    Design.newDesign
      .bind[MainHttpApiServerConfig].toSingletonProvider[ActorSystem] { system =>
        MainHttpApiServerConfig(system)
      }
      .bind[ResourceExecutionContext].toSingletonProvider[ActorSystem] { system =>
        system.dispatcher // akka.http のルーティングで使用するdispatcherを準備する。
      }
      .bind[MainHttpApiServerResource].to[DefaultBoxOfficeResource]
      // MyBoxOfficeResource 実装時にコメントアウトを外す (この行はそのまま)
      // .bind[MainHttpApiServerResource].to[MyBoxOfficeResource]
}
