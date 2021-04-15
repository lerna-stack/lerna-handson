package example.presentation

import akka.actor.typed.ActorSystem
import wvlet.airframe.Design

object PresentationDIDesign {
  // format: off
  lazy val design: Design =
    Design.newDesign
      .bind[PresentationExecutionContext].toSingletonProvider[ActorSystem[Nothing]] { system =>
        system.executionContext // TODO akka.http のルーティングで使用する ExecutionContext を準備する。
      }
      .bind[BoxOfficeResource].to[DefaultBoxOfficeResource]
      // MyBoxOfficeResource 実装時にコメントアウトを外す (この行はそのまま)
      .bind[BoxOfficeResource].to[MyBoxOfficeResource]
}
