package example

import com.wix.accord._
import com.wix.accord.dsl._

object ValidateExample extends App {

  case class Location(latitude: Double, longitude: Double)
  implicit val locationValidator: Validator[Location] = validator[Location] { location =>
    location.latitude is between(-90.0, 90.0)
    location.longitude is between(-180.0, 180.0)
  }

  // バリデータを明示的に与える場合
  val result1: Result = validate(Location(30, 120))(locationValidator)
  // バリデータを暗黙的に与える場合 (implicit)
  val result2: Result = validate(Location(30, 120))
}
