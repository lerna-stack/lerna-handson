package example.application.http.controller

import example.RouteSpecBase
import example.application.http._
import example.model.concert.service.BoxOfficeService
import example.readmodel.ConcertRepository
import example.testing.tags.ExerciseTest

@ExerciseTest
final class MyBoxOfficeResourceSpec extends RouteSpecBase with BoxOfficeResourceBehaviors {

  private def newResource(
      service: BoxOfficeService,
      repository: ConcertRepository,
  ): MainHttpApiServerResource = {
    new MyBoxOfficeResource(service, repository)(system.dispatcher)
  }

  classOf[MyBoxOfficeResource].getSimpleName should {
    behave like boxOfficeResource(newResource)
  }

}
