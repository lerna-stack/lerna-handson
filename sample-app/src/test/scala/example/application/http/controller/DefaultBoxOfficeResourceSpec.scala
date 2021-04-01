package example.application.http.controller

import example.RouteSpecBase
import example.application.http._
import example.usecase.{ BoxOfficeReadModelUseCase, BoxOfficeUseCase }

// CI fails when there is a warning such as Unused Imports
import scala.concurrent._

final class DefaultBoxOfficeResourceSpec extends RouteSpecBase with BoxOfficeResourceBehaviors {

  private def newResource(
      useCase: BoxOfficeUseCase,
      rmUseCase: BoxOfficeReadModelUseCase,
  ): MainHttpApiServerResource = {
    new DefaultBoxOfficeResource(useCase, rmUseCase)(system.dispatcher)
  }

  classOf[DefaultBoxOfficeResource].getSimpleName should {
    behave like boxOfficeResource(newResource)
  }

}
