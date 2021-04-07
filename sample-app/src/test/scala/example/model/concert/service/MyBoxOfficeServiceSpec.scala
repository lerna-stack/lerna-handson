package example.model.concert.service

import akka.actor.typed.ActorSystem
import example.model.ModelDiDesign
import example.model.concert.actor.ConcertActorClusterShardingFactory
import example.testing.tags.ExerciseTest
import testkit.AirframeDiSessionSupport
import wvlet.airframe.Design

@ExerciseTest
final class MyBoxOfficeServiceSpec
    extends BoxOfficeServiceSpecBase()
    with BoxOfficeServiceBehaviors
    with AirframeDiSessionSupport {
  override protected val design: Design =
    ModelDiDesign.design
      .bind[ActorSystem[Nothing]].toInstance(system)
      .bind[MyBoxOfficeService]
      .toInstanceProvider[ActorSystem[Nothing], ConcertActorClusterShardingFactory] { (system, factory) =>
        new MyBoxOfficeService(system, factory)
      }

  private def newService: BoxOfficeService = {
    session.build[MyBoxOfficeService]
  }

  classOf[MyBoxOfficeService].getSimpleName should {
    behave like boxOfficeService(newService)
  }

}
