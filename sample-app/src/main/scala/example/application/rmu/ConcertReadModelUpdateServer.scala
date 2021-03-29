package example.application.rmu

import akka.actor._
import akka.cluster.singleton._
import akka.pattern.{ BackoffOpts, BackoffSupervisor }
import example.readmodel.ConcertRepository

import scala.concurrent.duration._
import scala.language.postfixOps

final class ConcertReadModelUpdateServer(
    factory: ConcertEventSourceFactory,
    repository: ConcertRepository,
)(implicit
    system: ActorSystem,
) {
  private val singletonManagerProps: Props = ClusterSingletonManager.props(
    singletonProps = BackoffSupervisor.props(
      BackoffOpts.onFailure(
        ConcertDatabaseReadModelUpdater.props(factory, repository),
        childName = "RMU-instance",
        minBackoff = 3 seconds,
        maxBackoff = 30 seconds,
        randomFactor = 0.2,
      ),
    ),
    terminationMessage = ConcertDatabaseReadModelUpdater.Protocol.Terminate,
    settings = ClusterSingletonManagerSettings(system),
  )

  def start(): Unit = {
    system.actorOf(singletonManagerProps, "RMU")
  }
}
