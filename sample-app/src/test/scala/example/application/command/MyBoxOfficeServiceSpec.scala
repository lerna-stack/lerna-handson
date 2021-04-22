package example.application.command

import akka.actor.typed.ActorSystem
import example.adapter.command.BoxOfficeService
import example.application.ApplicationDIDesign
import example.testing.tags.ExerciseTest
import testkit.AirframeDiSessionSupport
import wvlet.airframe.Design

@ExerciseTest
final class MyBoxOfficeServiceSpec
    extends BoxOfficeServiceSpecBase()
    with BoxOfficeServiceBehaviors
    with AirframeDiSessionSupport {
  override protected val design: Design =
    ApplicationDIDesign.design
      .bind[ActorSystem[Nothing]].toInstance(system)

  private def newService: BoxOfficeService = {
    session.build[MyBoxOfficeService]
  }

  classOf[MyBoxOfficeService].getSimpleName should {
    behave like boxOfficeService(newService)
  }

}
