package example.application

import akka.actor.typed.ActorSystem
import example.ActorSpecBase
import example.application.command.actor.{ ConcertActorBehaviorFactory, MyConcertActor }
import example.testing.tags.ExerciseTest
import testkit.AirframeDiSessionSupport
import wvlet.airframe.Design

@ExerciseTest
final class MyConcertActorBindSpec extends ActorSpecBase() with AirframeDiSessionSupport {
  override protected val design: Design =
    ApplicationDIDesign.design
      .bind[ActorSystem[Nothing]].toInstance(system)

  // 演習で bind 成功を確認するために使用する
  // MyConcertActor のバインドに成功したら success になる
  "ConcertActorClusterShardingFactory should be bound to MyConcertActor" in {
    val behaviorFactory = session.build[ConcertActorBehaviorFactory]
    behaviorFactory shouldBe MyConcertActor
  }
}
