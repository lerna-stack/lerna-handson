package example.readmodel.rdb.projection

import akka.actor.typed.ActorSystem
import example.readmodel.DefaultReadModelDiDesign
import example.readmodel.rdb._
import example.testing.tags.ExerciseTest
import testkit.AirframeDiSessionSupport
import wvlet.airframe.Design

@ExerciseTest
final class MyConcertProjectionRepositorySpec
    extends DatabaseSpecBase()
    with AirframeDiSessionSupport
    with DatabaseConcertProjectionRepositoryBehaviors {

  override protected val design: Design =
    DefaultReadModelDiDesign.design
      .bind[ActorSystem[Nothing]].toInstance(system)
      .bind[DefaultConcertDatabaseServiceConfig].toInstance(
        new DefaultConcertDatabaseServiceConfig(databaseConfig),
      )

  protected override val databaseService: ConcertDatabaseService = session.build[ConcertDatabaseService]

  private val projectionRepository = session.build[MyConcertProjectionRepository]

  classOf[MyConcertProjectionRepository].getSimpleName should {
    behave like databaseConcertProjectionRepository(projectionRepository, databaseService)
  }

}
