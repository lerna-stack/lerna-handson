package example

import com.wix.accord._
import com.wix.accord.dsl._

object BasicExample extends App {

  case class Location(latitude: Double, longitude: Double)

  val locationValidator: Validator[Location] = validator[Location] { location =>
    location.latitude is between(-90.0, 90.0)
    location.longitude is between(-180.0, 180.0)
  }

}
