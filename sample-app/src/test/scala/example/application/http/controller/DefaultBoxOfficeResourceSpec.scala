package example.application.http.controller

import example.RouteSpecBase
import example.application.http._
import example.readmodel.ConcertRepository
import example.usecase.BoxOfficeUseCase

final class DefaultBoxOfficeResourceSpec extends RouteSpecBase with BoxOfficeResourceBehaviors {

  private def newResource(
      useCase: BoxOfficeUseCase,
      repository: ConcertRepository,
  ): MainHttpApiServerResource = {
    new DefaultBoxOfficeResource(useCase, repository)(system.dispatcher)
  }

  classOf[DefaultBoxOfficeResource].getSimpleName should {
    behave like boxOfficeResource(newResource)
  }

}
