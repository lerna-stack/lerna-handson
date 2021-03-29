package example

import akka.actor.ActorSystem
import akka.testkit.{ ImplicitSender, TestKit }
import org.scalatest.{ BeforeAndAfterAll, EitherValues, Inside }
import org.scalatest.concurrent.{ Eventually, ScalaFutures }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import testkit.{ AkkaPatienceConfigurationSupport, AkkaSpanScaleFactorSupport }

/** A test class which improve consistency and reduce boilerplate.
  *
  * @see [[https://www.scalatest.org/user_guide/defining_base_classes Defining base classes for your project]]
  */
abstract class ActorSpecBase(system: ActorSystem)
    extends TestKit(system)
    with AnyWordSpecLike
    with Matchers
    with Inside
    with BeforeAndAfterAll
    with ImplicitSender
    with Eventually
    with ScalaFutures
    with EitherValues
    with AkkaPatienceConfigurationSupport
    with AkkaSpanScaleFactorSupport {
  override protected def afterAll(): Unit = {
    try shutdown(system)
    finally super.afterAll()
  }
}
