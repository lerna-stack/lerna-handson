package example.model

import akka.actor.ActorSystem
import example.ActorSpecBase
import example.model.concert.actor._
import example.model.concert.service.ConcertActorClusterShardingFactory
import example.testing.tags.ExerciseTest
import testkit.AirframeDiSessionSupport
import wvlet.airframe.Design

@ExerciseTest
final class MyConcertActorBindSpec
    extends ActorSpecBase(ActorSystem("my-concert-actor-bind-spec"))
    with AirframeDiSessionSupport {
  override protected val design: Design =
    ModelDiDesign.design
      .bind[ActorSystem].toInstance(system)

  // 演習で bind 成功を確認するために使用する
  // MyConcertActor.props のバインドに成功したら success になる
  "ConcertActorClusterShardingFactory.ConcertActorProps bind to MyConcertActor.props" in {
    val props = session.build[ConcertActorClusterShardingFactory.ConcertActorProps]
    props shouldBe MyConcertActor.props
  }
}
