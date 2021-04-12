package example

import akka.actor.typed.ActorSystem
import example.application.http.MainHttpApiServerDiDesign
import example.application.rmu.DefaultReadModelUpdaterDiDesign
import example.model.ModelDiDesign
import example.readmodel.DefaultReadModelDiDesign
import wvlet.airframe.Design

import scala.concurrent.ExecutionContext

object MainDiDesign {
  def design(system: ActorSystem[Nothing]): Design = {
    Design.newDesign.withProductionMode
      .bind[ActorSystem[Nothing]].toInstance(system)
      .bind[ExecutionContext].toSingletonProvider[ActorSystem[Nothing]] { system =>
        // デフォルトでは system.executionContext を使うようにする。
        system.executionContext
      }
      .add(ModelDiDesign.design)
      .add(DefaultReadModelDiDesign.design)
      .add(DefaultReadModelUpdaterDiDesign.design)
      .add(MainHttpApiServerDiDesign.design)
  }
}
