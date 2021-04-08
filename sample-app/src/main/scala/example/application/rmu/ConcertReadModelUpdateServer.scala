package example.application.rmu

import akka.actor.typed.SupervisorStrategy
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.cluster.typed.{ ClusterSingleton, SingletonActor }
import akka.{ actor => classic }
import example.readmodel.ConcertRepository

import scala.concurrent.duration._

final class ConcertReadModelUpdateServer(
    factory: ConcertEventSourceFactory,
    repository: ConcertRepository,
)(implicit
    system: classic.ActorSystem,
) {
  private val singletonManager = ClusterSingleton(system.toTyped)

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
