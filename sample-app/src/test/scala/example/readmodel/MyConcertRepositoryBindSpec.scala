package example.readmodel

import akka.actor.typed.ActorSystem
import example.ActorSpecBase
import example.readmodel.rdb._
import example.testing.tags.ExerciseTest
import testkit.AirframeDiSessionSupport
import wvlet.airframe._

// TODO Remove this class
@ExerciseTest
final class MyConcertRepositoryBindSpec extends ActorSpecBase() with AirframeDiSessionSupport {
  override protected val design: Design =
    DefaultReadModelDiDesign.design
      .bind[ActorSystem[Nothing]].toInstance(system)

  // 演習で bind 成功を確認するために使用する
  // MyConcertRepository のバインドに成功したら success になる
  "MyConcertRepository bind to ConcertRepository" in {
    val repository = session.build[ConcertRepository]
    repository shouldBe a[MyConcertRepository]
  }
}
