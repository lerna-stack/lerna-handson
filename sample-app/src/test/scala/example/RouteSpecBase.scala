package example

import akka.actor.testkit.typed.scaladsl.LogCapturing
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.mockito.IdiomaticMockito
import org.scalatest.EitherValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import spray.json.DefaultJsonProtocol
import testkit.AkkaSpanScaleFactorSupport

/** A test class which improve consistency and reduce boilerplate.
  *
  * @see [[https://www.scalatest.org/user_guide/defining_base_classes Defining base classes for your project]]
  */
abstract class RouteSpecBase
    extends AnyWordSpecLike
    with Matchers
    with ScalatestRouteTest
    with ScalaFutures
    with EitherValues
    with DefaultJsonProtocol
    with SprayJsonSupport
    with IdiomaticMockito
    with AkkaSpanScaleFactorSupport
    with LogCapturing
