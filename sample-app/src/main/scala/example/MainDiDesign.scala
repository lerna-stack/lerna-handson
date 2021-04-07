package example

import akka.actor.typed.ActorSystem
import example.application.http.MainHttpApiServerDiDesign
import example.application.rmu.DefaultReadModelUpdaterDiDesign
import example.model.ModelDiDesign
import example.readmodel.DefaultReadModelDiDesign
import example.usecase.DefaultUseCaseDiDesign
import wvlet.airframe.Design

import scala.concurrent.ExecutionContext

object MainDiDesign {
  def design(system: ActorSystem[Nothing]): Design = {
    Design.newDesign.withProductionMode
      .bind[ActorSystem[Nothing]].toInstance(system)
      .bind[ExecutionContext].toSingletonProvider[ActorSystem[Nothing]] { system =>
        // デフォルトでは system.dispatcher を使うようにする。
        system.executionContext
      }
      .add(ModelDiDesign.design)
      .add(DefaultUseCaseDiDesign.design)
      .add(DefaultReadModelDiDesign.design)
      .add(DefaultReadModelUpdaterDiDesign.design)
      .add(MainHttpApiServerDiDesign.design)
  }
}
