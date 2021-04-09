package example.application.http.controller

import example.RouteSpecBase
import example.application.http._
import example.readmodel.ConcertRepository
import example.testing.tags.ExerciseTest
import example.usecase.BoxOfficeUseCase

@ExerciseTest
final class MyBoxOfficeResourceSpec extends RouteSpecBase with BoxOfficeResourceBehaviors {

  private def newResource(
      useCase: BoxOfficeUseCase,
      repository: ConcertRepository,
  ): MainHttpApiServerResource = {
    new MyBoxOfficeResource(useCase, repository)(system.dispatcher)
  }

  classOf[MyBoxOfficeResource].getSimpleName should {
    behave like boxOfficeResource(newResource)
  }

}
