package example.application.rmu

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorSystem, SupervisorStrategy }
import akka.cluster.typed.{ ClusterSingleton, SingletonActor }
import example.readmodel.ConcertRepository

import scala.concurrent.duration._

final class ConcertReadModelUpdateServer(
    factory: ConcertEventSourceFactory,
    repository: ConcertRepository,
)(implicit
    system: ActorSystem[Nothing],
) {
  private val singletonManager = ClusterSingleton(system)

  def start(): Unit = {
    val rmuActor = SingletonActor(
      Behaviors
        .supervise(ConcertDatabaseReadModelUpdater(factory, repository))
        .onFailure(SupervisorStrategy.restartWithBackoff(3.seconds, 30.seconds, 0.2)),
      "RMU",
    ).withStopMessage(ConcertDatabaseReadModelUpdater.Terminate)
    singletonManager.init(rmuActor)
  }

}
