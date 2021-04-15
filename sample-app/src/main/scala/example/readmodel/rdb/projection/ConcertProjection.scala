package example.readmodel.rdb.projection

import akka.actor.typed.ActorSystem
import akka.cluster.typed.{ ClusterSingleton, SingletonActor }
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.Offset
import akka.projection.eventsourced.EventEnvelope
import akka.projection.{ ProjectionBehavior, ProjectionId }
import akka.projection.eventsourced.scaladsl.EventSourcedProvider
import akka.projection.scaladsl.{ ExactlyOnceProjection, SourceProvider }
import akka.projection.slick.SlickProjection
import example.model.concert.ConcertEvent

final class ConcertProjection(projectionRepository: ConcertProjectionRepository)(implicit
    system: ActorSystem[Nothing],
) {

  // TODO Use Sharded Daemon Process
  // https://doc.akka.io/docs/akka-projection/current/running.html#running-with-sharded-daemon-process
  private val singleton = ClusterSingleton(system)

  def start(): Unit = {
    val proj = projection(ConcertEvent.tag)
    singleton.init(
      SingletonActor(
        ProjectionBehavior(proj),
        proj.projectionId.id,
      ),
    )
  }

  private def projection(tag: String): ExactlyOnceProjection[Offset, EventEnvelope[ConcertEvent]] = {
    val projectionId = ProjectionId("concerts", tag)
    SlickProjection.exactlyOnce(
      projectionId,
      sourceProvider(tag),
      projectionRepository.config,
      () => new ConcertProjectionHandler(projectionRepository),
    )
  }

  private def sourceProvider(tag: String): SourceProvider[Offset, EventEnvelope[ConcertEvent]] = {
    EventSourcedProvider.eventsByTag[ConcertEvent](
      system = system,
      readJournalPluginId = CassandraReadJournal.Identifier,
      tag = tag,
    )
  }

}
