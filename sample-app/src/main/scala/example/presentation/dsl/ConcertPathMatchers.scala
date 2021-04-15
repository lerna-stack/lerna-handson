package example.presentation.dsl

import akka.http.scaladsl.server.PathMatcher1
import akka.http.scaladsl.server.Directives._
import example.adapter.ConcertId
import example.presentation.ValidationException

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
