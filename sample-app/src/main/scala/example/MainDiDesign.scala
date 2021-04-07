package example

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.adapter.ClassicActorSystemOps
import akka.{ actor => classic }
import example.readmodel.DefaultReadModelDiDesign
import example.application.http.MainHttpApiServerDiDesign
import example.application.rmu.DefaultReadModelUpdaterDiDesign
import example.model.ModelDiDesign
import example.usecase.DefaultUseCaseDiDesign
import wvlet.airframe.Design

import scala.concurrent.ExecutionContext

object MainDiDesign {
  def design(system: classic.ActorSystem): Design = {
    Design.newDesign.withProductionMode
      .bind[classic.ActorSystem].toInstance(system)
      .bind[ActorSystem[Nothing]].toInstance(system.toTyped)
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
