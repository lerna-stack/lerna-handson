package example.usecase

import akka.actor.ActorSystem
import wvlet.airframe.Design

/** UseCase のデフォルト実装定義するDIデザイン
  */
object DefaultUseCaseDiDesign {
  lazy val design: Design =
    Design.newDesign
      .bind[BoxOfficeUseCase].to[DefaultBoxOfficeUseCase]
      .bind[DefaultBoxOfficeUseCase.UseCaseExecutionContext].toSingletonProvider[ActorSystem] { system =>
        system.dispatcher // TODO UseCase用のdispatcherを提供する。
      }
      .bind[BoxOfficeReadModelUseCase].to[DefaultBoxOfficeReadModelUseCase]
      .bind[DefaultBoxOfficeReadModelUseCase.UseCaseExecutionContext].toSingletonProvider[ActorSystem] { system =>
        system.dispatcher // TODO UseCase用のdispatcherを提供する。
      }
}
