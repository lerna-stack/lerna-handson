package example.application

import akka.actor.typed.ActorSystem
import example.ActorSpecBase
import example.application.projection.{ ConcertProjectionRepository, MyConcertProjectionRepository }
import example.readmodel.ReadModelDIDesign
import example.testing.tags.ExerciseTest
import testkit.AirframeDiSessionSupport
import wvlet.airframe._

@ExerciseTest
final class MyConcertProjectionRepositoryBindSpec extends ActorSpecBase() with AirframeDiSessionSupport {
  override protected val design: Design =
    ApplicationDIDesign.design
      .add(ReadModelDIDesign.design)
      .bind[ActorSystem[Nothing]].toInstance(system)

  // 演習で bind 成功を確認するために使用する
  // MyConcertProjectionRepository のバインドに成功したら success になる
  "ConcertProjectionRepository should be bound to MyConcertProjectionRepository" in {
    val service = session.build[ConcertProjectionRepository]
    service shouldBe a[MyConcertProjectionRepository]
  }

}
