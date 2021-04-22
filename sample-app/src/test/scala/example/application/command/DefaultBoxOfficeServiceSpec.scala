package example.application.command

import akka.actor.typed.ActorSystem
import example.adapter.command.BoxOfficeService
import example.application.ApplicationDIDesign
import testkit.AirframeDiSessionSupport
import wvlet.airframe.Design

final class DefaultBoxOfficeServiceSpec
    extends BoxOfficeServiceSpecBase()
    with BoxOfficeServiceBehaviors
    with AirframeDiSessionSupport {
  override protected val design: Design =
    ApplicationDIDesign.design
      .bind[ActorSystem[Nothing]].toInstance(system)

  private def newService: BoxOfficeService = {
    session.build[DefaultBoxOfficeService]
  }

  classOf[DefaultBoxOfficeService].getSimpleName should {
    behave like boxOfficeService(newService)
  }

}
