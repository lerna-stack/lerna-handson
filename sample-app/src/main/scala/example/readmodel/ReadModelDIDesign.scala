package example.readmodel

import akka.actor.typed.ActorSystem
import wvlet.airframe._

object ReadModelDIDesign {
  // format: off
  lazy val design: Design =
    newDesign
      .bind[ConcertDatabaseService].to[DefaultConcertDatabaseService]
      .bind[DefaultConcertDatabaseServiceConfig].toSingletonProvider[ActorSystem[Nothing]] { system =>
        DefaultConcertDatabaseServiceConfig(system)
      }
}
