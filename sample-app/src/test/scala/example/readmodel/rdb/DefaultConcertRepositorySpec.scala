package example.readmodel.rdb

import akka.actor.typed.ActorSystem
import example.readmodel.rdb.projection.DatabaseConcertProjectionRepositoryBehaviors
import example.readmodel.{ ConcertRepository, DefaultReadModelDiDesign }
import testkit.AirframeDiSessionSupport
import wvlet.airframe.Design

final class DefaultConcertRepositorySpec
    extends DatabaseConcertRepositorySpecBase()
    with DatabaseConcertRepositoryBehaviors
    with DatabaseConcertProjectionRepositoryBehaviors
    with AirframeDiSessionSupport {
  override protected val design: Design =
    DefaultReadModelDiDesign.design
      .bind[ActorSystem[Nothing]].toInstance(system)
      .bind[DefaultConcertDatabaseServiceConfig].toInstance(
        new DefaultConcertDatabaseServiceConfig(databaseConfig),
      )

  protected override val databaseService: ConcertDatabaseService = session.build[ConcertDatabaseService]

  private val repository: ConcertRepository = session.build[DefaultConcertRepository]

  classOf[DefaultConcertRepository].getSimpleName should {
    behave like databaseConcertRepository(repository, databaseService)
    behave like databaseConcertProjectionRepository(repository, databaseService)
  }

}
