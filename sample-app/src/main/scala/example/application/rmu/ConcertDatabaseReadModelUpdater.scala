package example.application.rmu

import akka.actor._
import akka.pattern.pipe
import akka.persistence.query.Offset
import akka.stream._
import akka.stream.scaladsl._
import akka.Done
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import example.readmodel.ConcertRepository

import scala.concurrent._

object ConcertDatabaseReadModelUpdater {
  sealed trait Command
  case object Terminate extends Command

  def apply(sourceFactory: ConcertEventSourceFactory, repository: ConcertRepository): Behavior[Command] = {
    // TODO AKka Typed のみを使って実装する
    Behaviors.setup { context =>
      val classic = context.actorOf(props(sourceFactory, repository))
      context.watch(classic)
      Behaviors
        .receiveMessage[Command] { message =>
          classic.tell(message, context.self.toClassic)
          Behaviors.same
        }.receiveSignal {
          case (_, akka.actor.typed.Terminated(_)) =>
            Behaviors.stopped
        }
    }
  }

  // TODO 削除する
  private def props(sourceFactory: ConcertEventSourceFactory, repository: ConcertRepository): Props = {
    Props(new ConcertDatabaseReadModelUpdater(sourceFactory, repository))
  }

  // Internal Commands
  // 更新処理が開始した
  private final case class UpdateGraphStarted(killSwitch: KillSwitch)
  // 更新処理が完了した
  private case object UpdateGraphStopped
}

// TODO implement using akka typed
final class ConcertDatabaseReadModelUpdater(
    sourceFactory: ConcertEventSourceFactory,
    repository: ConcertRepository,
) extends Actor
    with ActorLogging
    with Stash {
  import ConcertDatabaseReadModelUpdater._

  import context.dispatcher
  implicit private val materializer: Materializer = Materializer(context)
  private val LogName: String                     = "RMU"

  runUpdateGraph()

  override def receive: Receive = receiveInStarting

  private def receiveInStarting: Receive = {
    case started: UpdateGraphStarted =>
      log.info("Update Graph started with {}.", started.killSwitch)
      context.become(receiveInRunning(started.killSwitch))
      unstashAll()
    case failure: Status.Failure =>
      // Supervisor で処理してもらう
      throw failure.cause
    case _ =>
      stash()
  }

  private def receiveInRunning(killSwitch: KillSwitch): Receive = {
    case failure: Status.Failure =>
      // Supervisor で処理してもらう
      throw failure.cause
    case Terminate =>
      log.info("Received termination request.")
      killSwitch.shutdown()
    case UpdateGraphStopped =>
      log.info("Update Graph stopped, and then the Actor {} stop itself.", self)
      context.stop(self)
  }

  private def runUpdateGraph(): Unit = {
    repository
      .fetchConcertEventOffset()
      .map(createUpdateRunnableGraph)
      .flatMap(updateGraph => {
        val (killSwitch, running) = updateGraph.run()
        self ! UpdateGraphStarted(killSwitch)
        running
      })
      .map(_ => UpdateGraphStopped)
      .pipeTo(self)
  }

  private def createUpdateRunnableGraph(offset: Offset): RunnableGraph[(KillSwitch, Future[Done])] = {
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
