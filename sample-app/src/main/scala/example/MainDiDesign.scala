package example

import akka.actor.ActorSystem
import example.readmodel.DefaultReadModelDiDesign
import example.application.http.MainHttpApiServerDiDesign
import example.application.rmu.DefaultReadModelUpdaterDiDesign
import example.model.ModelDiDesign
import example.usecase.DefaultUseCaseDiDesign
import wvlet.airframe.Design

import scala.concurrent.ExecutionContext

object MainDiDesign {
  def design(system: ActorSystem): Design = {
    Design.newDesign.withProductionMode
      .bind[ActorSystem].toInstance(system)
      .bind[ExecutionContext].toSingletonProvider[ActorSystem] { system =>
        // デフォルトでは system.dispatcher を使うようにする。
        system.dispatcher
      }
      .add(ModelDiDesign.design)
      .add(DefaultUseCaseDiDesign.design)
      .add(DefaultReadModelDiDesign.design)
      .add(DefaultReadModelUpdaterDiDesign.design)
      .add(MainHttpApiServerDiDesign.design)
  }
}
