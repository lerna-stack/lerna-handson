package example.model.concert.service

import akka.actor.ActorSystem
import example.model.ModelDiDesign
import testkit.AirframeDiSessionSupport
import wvlet.airframe.Design

final class DefaultBoxOfficeServiceSpec
    extends BoxOfficeServiceSpecBase("default-box-office-service-spec")
    with BoxOfficeServiceBehaviors
    with AirframeDiSessionSupport {
  override protected val design: Design =
    ModelDiDesign.design
      .bind[ActorSystem].toInstance(system.classicSystem)
      .bind[DefaultBoxOfficeService]
      .toInstanceProvider[ActorSystem, ConcertActorClusterShardingFactory] { (system, factory) =>
        new DefaultBoxOfficeService(system, factory)
      }

  private def newService: BoxOfficeService = {
    session.build[DefaultBoxOfficeService]
  }

  classOf[DefaultBoxOfficeService].getSimpleName should {
    behave like boxOfficeService(newService)
  }

}
