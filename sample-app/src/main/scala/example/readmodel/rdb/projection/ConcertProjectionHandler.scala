package example.readmodel.rdb.projection

import akka.Done
import akka.projection.eventsourced.EventEnvelope
import akka.projection.slick.SlickHandler
import example.model.concert.ConcertEvent
import org.slf4j.LoggerFactory
import slick.dbio.DBIO

final class ConcertProjectionHandler(repository: ConcertProjectionRepository)
    extends SlickHandler[EventEnvelope[ConcertEvent]] {
  private val logger = LoggerFactory.getLogger(getClass)

  override def process(envelope: EventEnvelope[ConcertEvent]): DBIO[Done] = {
    logger.info(s"Handle ${envelope}")
    repository.update(envelope.event)
  }

}
