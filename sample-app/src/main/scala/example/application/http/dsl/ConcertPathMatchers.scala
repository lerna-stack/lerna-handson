package example.application.http.dsl

import akka.http.scaladsl.server.PathMatcher1
import akka.http.scaladsl.server.Directives._
import example.application.http.controller.ValidationException
import example.model.concert.ConcertId

/** domain.concert の Value Object を取り出す PathMatcher を定義する。
  */
object ConcertPathMatchers {
  val ConcertIdentifier: PathMatcher1[ConcertId] =
    Segment.map { rawValue =>
      ConcertId
        .fromString(rawValue)
        .left.map(error => new ValidationException(error))
        .toTry.get
    }
}
