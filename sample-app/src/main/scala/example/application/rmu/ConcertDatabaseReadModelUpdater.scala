package example.application.rmu

import akka.Done
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{ ActorContext, Behaviors }
import akka.persistence.query.Offset
import akka.stream._
import akka.stream.scaladsl._
import example.readmodel.ConcertRepository

import scala.concurrent._
import scala.util.{ Failure, Success }

object ConcertDatabaseReadModelUpdater {
  sealed trait Command
  case object Terminate                                               extends Command
  private final case class UpdateGraphStarted(killSwitch: KillSwitch) extends Command
  private case object UpdateGraphStopped                              extends Command
  private final case class UpdateGraphFailed(cause: Throwable)        extends Command

  def apply(sourceFactory: ConcertEventSourceFactory, repository: ConcertRepository): Behavior[Command] = {
    Behaviors.setup { context =>
      val updater = new ConcertDatabaseReadModelUpdater(context, sourceFactory, repository)
      updater.receiveInStarting()
    }
  }

}

final class ConcertDatabaseReadModelUpdater private (
    context: ActorContext[ConcertDatabaseReadModelUpdater.Command],
    sourceFactory: ConcertEventSourceFactory,
    repository: ConcertRepository,
) {
  import ConcertDatabaseReadModelUpdater._

  private val LogName: String          = "RMU"
  private val StashBufferCapacity: Int = 1000

  private implicit val executionContext: ExecutionContext = context.system.executionContext
  private implicit val materializer: Materializer         = Materializer(context)

  runUpdateGraph()

  private def receiveInStarting(): Behavior[Command] = {
    Behaviors.withStash(StashBufferCapacity) { stashBuffer =>
      Behaviors.receiveMessage[Command] {
        case UpdateGraphStarted(killSwitch) =>
          context.log.info("Update Graph started with {}.", killSwitch)
          stashBuffer.unstashAll(receiveInRunning(killSwitch))
        case UpdateGraphFailed(cause) =>
          // supervisor に復旧を任せる
          throw cause
        case other =>
          // stash が full の場合は、StashOverflowException が発生するが、 supervisor に復旧を任せる
          stashBuffer.stash(other)
          Behaviors.same
      }
    }
  }

  private def receiveInRunning(killSwitch: KillSwitch): Behavior[Command] =
    Behaviors.receiveMessage[Command] {
      case Terminate =>
        context.log.info("Received termination request.")
        killSwitch.shutdown()
        Behaviors.same
      case UpdateGraphStopped =>
        Behaviors.stopped
      case UpdateGraphFailed(cause) =>
        // supervisor に復旧を任せる
        throw cause
      case unexpectedStarted: UpdateGraphStarted =>
        // 予期していないメッセージのため、動いているかもしれない Streams は停止する
        killSwitch.shutdown()
        unexpectedStarted.killSwitch.shutdown()
        // supervisor に復旧は任せる
        throw new IllegalStateException("Got unexpected UpdateGraphStarted.")
    }

  private[this] def runUpdateGraph(): Unit = {
    val rmuRunning = repository
      .fetchConcertEventOffset()
      .map(createUpdateRunnableGraph)
      .flatMap(updateGraph => {
        val (killSwitch, running) = updateGraph.run()
        context.self ! UpdateGraphStarted(killSwitch)
        running
      })
    context.pipeToSelf(rmuRunning) {
      case Success(_)     => UpdateGraphStopped
      case Failure(cause) => UpdateGraphFailed(cause)
    }
  }

  private[this] def createUpdateRunnableGraph(offset: Offset): RunnableGraph[(KillSwitch, Future[Done])] = {
    sourceFactory
      .createEventStream(offset)
      .viaMat(KillSwitches.single)(Keep.right)
      .log(LogName)
      .addAttributes(
        Attributes.logLevels(
          // ログが確認しやすいように INFO レベルにする
          onElement = Attributes.LogLevels.Info,
        ),
      )
      .mapAsync(1) { eventEnvelope =>
        repository.updateByConcertEvent(eventEnvelope.event, eventEnvelope.offset)
      }
      .toMat(Sink.ignore)(Keep.both)
  }
}
