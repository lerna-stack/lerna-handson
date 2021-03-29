package example.application.rmu

import akka.NotUsed
import akka.actor.ActorSystem
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.{ Offset, PersistenceQuery }
import akka.stream.scaladsl.Source
import example.model.concert.ConcertEvent
import org.slf4j.LoggerFactory

/** Cassandra からイベントを読み込むストリームを生成する
  * @param system
  */
final class CassandraConcertEventSourceFactory(system: ActorSystem) extends ConcertEventSourceFactory {
  private val logger  = LoggerFactory.getLogger(this.getClass)
  private val queries = PersistenceQuery(system).readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)

  override def createEventStream(offset: Offset): Source[ConcertEventEnvelope, NotUsed] = {
    queries.eventsByTag(ConcertEvent.tag, offset).map { eventEnvelop =>
      logger.info(s"eventsByTag => $eventEnvelop")
      eventEnvelop.event match {
        case event: ConcertEvent =>
          ConcertEventEnvelope(eventEnvelop.offset, event)
        case _ =>
          throw new IllegalArgumentException()
      }
    }
  }
}
