package example.application

import akka.actor.typed.ActorSystem
import example.ActorSpecBase
import example.adapter.command.BoxOfficeService
import example.application.command.MyBoxOfficeService
import example.testing.tags.ExerciseTest
import testkit.AirframeDiSessionSupport
import wvlet.airframe._

@ExerciseTest
final class MyBoxOfficeServiceBindSpec extends ActorSpecBase() with AirframeDiSessionSupport {
  override protected val design: Design =
    ApplicationDIDesign.design
      .bind[ActorSystem[Nothing]].toInstance(system)

  // 演習で bind 成功を確認するために使用する
  // MyBoxOfficeService のバインドに成功したら success になる
  "BoxOfficeService should be bound to MyBoxOfficeService" in {
    val service = session.build[BoxOfficeService]
    service shouldBe a[MyBoxOfficeService]
  }
}
