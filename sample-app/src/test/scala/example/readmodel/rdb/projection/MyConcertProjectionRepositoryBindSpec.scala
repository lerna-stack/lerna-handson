package example.readmodel.rdb.projection

import akka.actor.typed.ActorSystem
import example.ActorSpecBase
import example.readmodel.DefaultReadModelDiDesign
import example.testing.tags.ExerciseTest
import testkit.AirframeDiSessionSupport
import wvlet.airframe._

@ExerciseTest
final class MyConcertProjectionRepositoryBindSpec extends ActorSpecBase() with AirframeDiSessionSupport {
  override protected val design: Design =
    DefaultReadModelDiDesign.design
      .bind[ActorSystem[Nothing]].toInstance(system)

  // 演習で bind 成功を確認するために使用する
  // MyBoxOfficeService のバインドに成功したら success になる
  "ConcertProjectionRepository should be bound to MyConcertProjectionRepository" in {
    val service = session.build[ConcertProjectionRepository]
    service shouldBe a[MyConcertProjectionRepository]
  }

}
