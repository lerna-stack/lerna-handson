package example.model.concert.service

import akka.actor.ActorSystem
import example.model.ModelDiDesign
import example.testing.tags.ExerciseTest
import testkit.AirframeDiSessionSupport
import wvlet.airframe.Design

@ExerciseTest
final class MyBoxOfficeServiceSpec
    extends BoxOfficeServiceSpecBase("my-box-office-service-spec")
    with BoxOfficeServiceBehaviors
    with AirframeDiSessionSupport {
  override protected val design: Design =
    ModelDiDesign.design
      .bind[ActorSystem].toInstance(system)
      .bind[MyBoxOfficeService]
      .toInstanceProvider[ActorSystem, ConcertActorClusterShardingFactory] { (system, factory) =>
        new MyBoxOfficeService(system, factory)
      }

  private def newService: BoxOfficeService = {
    session.build[MyBoxOfficeService]
  }

  classOf[MyBoxOfficeService].getSimpleName should {
    behave like boxOfficeService(newService)
  }

}
