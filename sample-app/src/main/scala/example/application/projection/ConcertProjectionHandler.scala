package example.application.projection

import akka.Done
import akka.projection.eventsourced.EventEnvelope
import akka.projection.slick.SlickHandler
import example.application.ConcertEvent
import org.slf4j.LoggerFactory
import slick.dbio.DBIO

final class ConcertProjectionHandler(repository: ConcertProjectionRepository)
    extends SlickHandler[EventEnvelope[ConcertEvent]] {
  private val logger = LoggerFactory.getLogger(getClass)

  override def process(envelope: EventEnvelope[ConcertEvent]): DBIO[Done] = {
    logger.info(s"Handle ${format(envelope)}")
    repository.update(envelope.event)
  }

  /** [[EventEnvelope]] のフィールドを含めた文字列にフォーマットする
    *
    * [[EventEnvelope#toString()]] では達成できないため
    */
  private def format(envelope: EventEnvelope[ConcertEvent]): String = {
    s"EventEnvelope(" +
    s"offset=${envelope.offset}, " +
    s"""persistenceId="${envelope.persistenceId}", """ +
    s"sequenceNr=${envelope.sequenceNr}, " +
    s"event=${envelope.event}, " +
    s"timestamp=${envelope.timestamp})"
  }

}
