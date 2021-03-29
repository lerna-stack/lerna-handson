package example

import com.wix.accord._
import com.wix.accord.dsl._

object ValidationResultExample extends App {

  case class Location(latitude: Double, longitude: Double)
  implicit val locationValidator: Validator[Location] = validator[Location] { location =>
    location.latitude is between(-90.0, 90.0)
    location.longitude is between(-180.0, 180.0)
  }

  // バリデーションに成功する場合
  val validLocation                 = Location(30, 120)
  val result: com.wix.accord.Result = validate(validLocation)
  assert(result == Success)

  // バリデーションに失敗する場合
  val invalidLocation                = Location(120, 30)
  val failure: com.wix.accord.Result = validate(invalidLocation)
  assert(failure.isInstanceOf[com.wix.accord.Failure])

  // com.wix.accord.Failure のインスタンスには、
  // バリデーション違反になったものが含まれている。
  // violations: Set[com.wix.accord.Violation]
}
