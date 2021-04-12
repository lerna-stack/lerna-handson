package example.application.http.controller

import example.RouteSpecBase
import example.application.http._
import example.model.concert.service.BoxOfficeService
import example.readmodel.ConcertRepository

final class DefaultBoxOfficeResourceSpec extends RouteSpecBase with BoxOfficeResourceBehaviors {

  private def newResource(
      service: BoxOfficeService,
      repository: ConcertRepository,
  ): MainHttpApiServerResource = {
    new DefaultBoxOfficeResource(service, repository)(system.dispatcher)
  }

  classOf[DefaultBoxOfficeResource].getSimpleName should {
    behave like boxOfficeResource(newResource)
  }

}
