package example.presentation

import example.RouteSpecBase
import example.adapter.ConcertRepository
import example.adapter.command.BoxOfficeService

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
