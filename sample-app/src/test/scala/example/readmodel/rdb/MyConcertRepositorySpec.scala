package example.readmodel.rdb

import akka.actor.typed.ActorSystem
import example.readmodel.{ ConcertRepository, DefaultReadModelDiDesign }
import example.testing.tags.ExerciseTest
import testkit.AirframeDiSessionSupport
import wvlet.airframe.Design

@ExerciseTest
final class MyConcertRepositorySpec
    extends DatabaseConcertRepositorySpecBase()
    with DatabaseConcertRepositoryBehaviors
    with AirframeDiSessionSupport {
  override protected val design: Design =
    DefaultReadModelDiDesign.design
      .bind[ActorSystem[Nothing]].toInstance(system)
      .bind[DefaultConcertDatabaseServiceConfig].toInstance(
        new DefaultConcertDatabaseServiceConfig(databaseConfig),
      )

  protected override val databaseService: ConcertDatabaseService = session.build[ConcertDatabaseService]

  private val repository: ConcertRepository = session.build[MyConcertRepository]

  classOf[MyConcertRepository].getSimpleName should {
    behave like databaseConcertRepository(repository, databaseService)
  }

}
