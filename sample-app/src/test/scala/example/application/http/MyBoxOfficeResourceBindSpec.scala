package example.application.http

import akka.actor.typed.ActorSystem
import example.ActorSpecBase
import example.application.http.controller.MyBoxOfficeResource
import example.readmodel.ConcertRepository
import example.testing.tags.ExerciseTest
import example.usecase._
import org.mockito.MockitoSugar
import testkit.AirframeDiSessionSupport
import wvlet.airframe.Design

@ExerciseTest
final class MyBoxOfficeResourceBindSpec extends ActorSpecBase() with AirframeDiSessionSupport with MockitoSugar {
  // TODO ActorSystem も受け取らないようにする

  override protected val design: Design =
    MainHttpApiServerDiDesign.design
      .bind[ActorSystem[Nothing]].toInstance(system)
      .bind[BoxOfficeUseCase].toInstance(mock[BoxOfficeUseCase])
      .bind[ConcertRepository].toInstance(mock[ConcertRepository])

  // 演習で bind 成功を確認するために使用する
  // MyBoxOfficeResource のバインドに成功したら success になる
  "MyBoxOfficeResource bind to MainHttpApiServerResource" in {
    val resource = session.build[MainHttpApiServerResource]
    resource shouldBe a[MyBoxOfficeResource]
  }
}
