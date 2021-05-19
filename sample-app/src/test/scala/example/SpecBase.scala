package example

import akka.actor.testkit.typed.scaladsl.LogCapturing
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{ EitherValues, Inside }

/** A test class which improve consistency and reduce boilerplate.
  *
  * @see [[https://www.scalatest.org/user_guide/defining_base_classes Defining base classes for your project]]
  */
abstract class SpecBase extends AnyWordSpecLike with Matchers with Inside with EitherValues with LogCapturing
