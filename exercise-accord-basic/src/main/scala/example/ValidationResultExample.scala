package example

import com.wix.accord._
import com.wix.accord.dsl._

object ValidationResultExample extends App {

  case class Location(latitude: Double, longitude: Double)
  implicit val locationValidator: Validator[Location] = validator[Location] { location =>
    location.latitude is between(-90.0, 90.0)
    location.longitude is between(-180.0, 180.0)
  }

  // バリデーション結果はパターンマッチ等で確認できる
  val validLocation                        = Location(30, 120)
  val successResult: com.wix.accord.Result = validate(validLocation)
  successResult match {
    case Success =>
      println("success")
    case Failure(violations: Set[Violation]) =>
      println(s"failure: $violations")
  }

}
