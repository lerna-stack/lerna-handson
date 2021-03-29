package example.model

import example.model.concert.actor._
import example.model.concert.service._
import wvlet.airframe._

object ModelDiDesign {
  // format: off
  lazy val design: Design =
    newDesign
      .bind[BoxOfficeService].to[DefaultBoxOfficeService]
      .bind[ConcertActorClusterShardingFactory].toSingleton
      .bind[ConcertActorClusterShardingFactory.ConcertActorProps].toInstance(DefaultConcertActor.props)
      // MyConcertActor実装時にコメントアウトを外す (この行はそのまま)
      // .bind[ConcertActorClusterShardingFactory.ConcertActorProps].toInstance(MyConcertActor.props)
      // MyBoxOfficeService実装時にコメントアウトを外す (この行はそのまま)
      // .bind[BoxOfficeService].to[MyBoxOfficeService]
}
