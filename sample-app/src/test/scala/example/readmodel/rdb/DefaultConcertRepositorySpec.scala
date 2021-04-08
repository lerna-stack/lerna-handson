package example.readmodel.rdb

import akka.actor.ActorSystem
import example.readmodel.{ ConcertRepository, DefaultReadModelDiDesign }
import testkit.AirframeDiSessionSupport
import wvlet.airframe.Design

final class DefaultConcertRepositorySpec
    extends DatabaseConcertRepositorySpecBase()
    with DatabaseConcertRepositoryBehaviors
    with AirframeDiSessionSupport {
  override protected val design: Design =
    DefaultReadModelDiDesign.design
      .bind[ActorSystem].toInstance(system.classicSystem)
      .bind[DefaultConcertDatabaseServiceConfig].toInstance(
        new DefaultConcertDatabaseServiceConfig(databaseConfig),
      )

  protected override val databaseService: ConcertDatabaseService = session.build[ConcertDatabaseService]

  private val repository: ConcertRepository = session.build[DefaultConcertRepository]

  classOf[DefaultConcertRepository].getSimpleName should {
    behave like databaseConcertRepository(repository, databaseService)
  }

}
