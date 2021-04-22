package example.application

import akka.actor.typed.ActorSystem
import example.adapter.command.BoxOfficeService
import example.adapter.projection.ConcertProjection
import example.adapter.query.ConcertRepository
import example.application.command._
import example.application.command.actor._
import example.application.projection._
import example.application.query.DefaultConcertRepository
import wvlet.airframe.{ newDesign, Design }

object ApplicationDIDesign {
  // format: off
  lazy val design: Design =
      newDesign
        .bind[ApplicationExecutionContext].toSingletonProvider[ActorSystem[Nothing]] { system =>
          system.executionContext // TODO application で使用する ExecutionContext を準備する。
        }
        .bind[BoxOfficeService].to[DefaultBoxOfficeService]
        // MyBoxOfficeService実装時にコメントアウトを外す (この行はそのまま)
        // .bind[BoxOfficeService].to[MyBoxOfficeService]
        .bind[ConcertActorBehaviorFactory].toInstance(DefaultConcertActor)
        // MyConcertActor実装時にコメントアウトを外す (この行はそのまま)
        // .bind[ConcertActorBehaviorFactory].toInstance(MyConcertActor)
        .bind[ConcertRepository].to[DefaultConcertRepository]
        .bind[ConcertProjection].to[DefaultConcertProjection]
        .bind[ConcertProjectionRepository].to[DefaultConcertProjectionRepository]
        // MyConcertProjectionRepository実装時にコメントアウトを外す (この行はそのまま)
        // .bind[ConcertProjectionRepository].to[MyConcertProjectionRepository]

}
