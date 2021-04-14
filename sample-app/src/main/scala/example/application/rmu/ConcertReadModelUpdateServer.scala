package example.application.rmu

import akka.actor.typed.ActorSystem
import example.readmodel.rdb.projection.{ ConcertProjection, ConcertProjectionRepository }

// TODO remove this class to make `sample-app` simple
final class ConcertReadModelUpdateServer(
    projectionRepository: ConcertProjectionRepository,
)(implicit
    system: ActorSystem[Nothing],
) {

  val projection = new ConcertProjection(projectionRepository)

  def start(): Unit = {
    projection.start()
  }

}
