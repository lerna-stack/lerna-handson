package example.presentation

import example.RouteSpecBase
import example.adapter.command.BoxOfficeService
import example.adapter.query.ConcertRepository
import example.testing.tags.ExerciseTest

@ExerciseTest
final class MyBoxOfficeResourceSpec extends RouteSpecBase with BoxOfficeResourceBehaviors {

  private def newResource(
      service: BoxOfficeService,
      repository: ConcertRepository,
  ): BoxOfficeResource = {
    new MyBoxOfficeResource(service, repository)(system.dispatcher)
  }

  classOf[MyBoxOfficeResource].getSimpleName should {
    behave like boxOfficeResource(newResource)
  }

}
