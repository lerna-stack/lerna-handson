package example.application.http.controller

import example.RouteSpecBase
import example.application.http._
import example.testing.tags.ExerciseTest
import example.usecase.{ BoxOfficeReadModelUseCase, BoxOfficeUseCase }

@ExerciseTest
final class MyBoxOfficeResourceSpec extends RouteSpecBase with BoxOfficeResourceBehaviors {

  private def newResource(
      useCase: BoxOfficeUseCase,
      rmUseCase: BoxOfficeReadModelUseCase,
  ): MainHttpApiServerResource = {
    new MyBoxOfficeResource(useCase, rmUseCase)(system.dispatcher)
  }

  classOf[MyBoxOfficeResource].getSimpleName should {
    behave like boxOfficeResource(newResource)
  }

}
