package example.usecase

import akka.actor.typed.ActorSystem
import wvlet.airframe.Design

/** UseCase のデフォルト実装定義するDIデザイン
  */
object DefaultUseCaseDiDesign {
  lazy val design: Design =
    Design.newDesign
      .bind[BoxOfficeUseCase].to[DefaultBoxOfficeUseCase]
      .bind[DefaultBoxOfficeUseCase.UseCaseExecutionContext].toSingletonProvider[ActorSystem[Nothing]] { system =>
        system.executionContext // TODO UseCase用の ExecutionContext を提供する。
      }
      .bind[BoxOfficeReadModelUseCase].to[DefaultBoxOfficeReadModelUseCase]
      .bind[DefaultBoxOfficeReadModelUseCase.UseCaseExecutionContext].toSingletonProvider[ActorSystem[Nothing]] {
        system =>
          system.executionContext // TODO UseCase用の ExecutionContext を提供する。
      }
}
