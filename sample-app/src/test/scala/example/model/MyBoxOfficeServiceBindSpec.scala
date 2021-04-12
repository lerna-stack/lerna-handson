package example.model

import akka.actor.typed.ActorSystem
import example.ActorSpecBase
import example.model.concert.service.{ BoxOfficeService, MyBoxOfficeService }
import example.testing.tags.ExerciseTest
import testkit.AirframeDiSessionSupport
import wvlet.airframe._

@ExerciseTest
final class MyBoxOfficeServiceBindSpec extends ActorSpecBase() with AirframeDiSessionSupport {
  override protected val design: Design =
    ModelDiDesign.design
      .bind[ActorSystem[Nothing]].toInstance(system)

  // 演習で bind 成功を確認するために使用する
  // MyBoxOfficeService のバインドに成功したら success になる
  "BoxOfficeService bind to MyBoxOfficeService" in {
    val service = session.build[BoxOfficeService]
    service shouldBe a[MyBoxOfficeService]
  }
}
