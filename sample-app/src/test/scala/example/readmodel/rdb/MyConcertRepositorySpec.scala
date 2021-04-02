package example.readmodel.rdb

import akka.actor.ActorSystem
import example.readmodel.{ ConcertRepository, DefaultReadModelDiDesign }
import example.testing.tags.ExerciseTest
import testkit.AirframeDiSessionSupport
import wvlet.airframe.Design

@ExerciseTest
final class MyConcertRepositorySpec
    extends DatabaseConcertRepositorySpecBase("my-concert-repository-spec")
    with DatabaseConcertRepositoryBehaviors
    with AirframeDiSessionSupport {
  override protected val design: Design =
    DefaultReadModelDiDesign.design
      .bind[ActorSystem].toInstance(system.classicSystem)
      .bind[DefaultConcertDatabaseServiceConfig].toInstance(
        new DefaultConcertDatabaseServiceConfig(databaseConfig),
      )

  protected override val databaseService: ConcertDatabaseService = session.build[ConcertDatabaseService]

  private val repository: ConcertRepository = session.build[MyConcertRepository]

  classOf[MyConcertRepository].getSimpleName should {
    behave like databaseConcertRepository(repository, databaseService)
  }

}
