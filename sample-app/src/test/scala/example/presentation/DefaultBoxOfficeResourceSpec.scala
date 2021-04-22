package example.presentation

import example.RouteSpecBase
import example.adapter.command.BoxOfficeService
import example.adapter.query.ConcertRepository

final class DefaultBoxOfficeResourceSpec extends RouteSpecBase with BoxOfficeResourceBehaviors {

  private def newResource(
      service: BoxOfficeService,
      repository: ConcertRepository,
  ): BoxOfficeResource = {
    new DefaultBoxOfficeResource(service, repository)(system.dispatcher)
  }

  classOf[DefaultBoxOfficeResource].getSimpleName should {
    behave like boxOfficeResource(newResource)
  }

}
