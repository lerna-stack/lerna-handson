package example.entrypoint

import akka.actor.typed.ActorSystem
import example.application.ApplicationDIDesign
import example.presentation.PresentationDIDesign
import example.readmodel.ReadModelDIDesign
import wvlet.airframe.Design

import scala.concurrent.ExecutionContext

object MainDIDesign {
  def design(system: ActorSystem[Nothing]): Design = {
    Design.newDesign.withProductionMode
      .bind[ActorSystem[Nothing]].toInstance(system)
      .bind[ExecutionContext].toSingletonProvider[ActorSystem[Nothing]] { system =>
        // デフォルトでは system.executionContext を使うようにする。
        system.executionContext
      }
      .add(ApplicationDIDesign.design)
      .add(ReadModelDIDesign.design)
      .add(PresentationDIDesign.design)
      .bind[MainHttpApiServerConfig].toSingletonProvider[ActorSystem[Nothing]] { system =>
        MainHttpApiServerConfig(system)
      }
  }
}
