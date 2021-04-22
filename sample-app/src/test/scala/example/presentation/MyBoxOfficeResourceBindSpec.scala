package example.presentation

import akka.actor.typed.ActorSystem
import example.ActorSpecBase
import example.adapter.{ BoxOfficeService, ConcertRepository }
import example.testing.tags.ExerciseTest
import org.mockito.MockitoSugar
import testkit.AirframeDiSessionSupport
import wvlet.airframe.Design

@ExerciseTest
final class MyBoxOfficeResourceBindSpec extends ActorSpecBase() with AirframeDiSessionSupport with MockitoSugar {
  // TODO ActorSystem も受け取らないようにする

  override protected val design: Design =
    PresentationDIDesign.design
      .bind[ActorSystem[Nothing]].toInstance(system)
      .bind[BoxOfficeService].toInstance(mock[BoxOfficeService])
      .bind[ConcertRepository].toInstance(mock[ConcertRepository])

  // 演習で bind 成功を確認するために使用する
  // MyBoxOfficeResource のバインドに成功したら success になる
  "MyBoxOfficeResource should be bound to BoxOfficeResource" in {
    val resource = session.build[BoxOfficeResource]
    resource shouldBe a[MyBoxOfficeResource]
  }
}
