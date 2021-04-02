package example

import akka.actor.ActorSystem
import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.typed.scaladsl.adapter._
import org.scalatest.concurrent.{ Eventually, ScalaFutures }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{ BeforeAndAfterAll, EitherValues, Inside }
import testkit.AkkaTypedSpanScaleFactorSupport

/** A test class which improve consistency and reduce boilerplate.
  *
  * @see [[https://www.scalatest.org/user_guide/defining_base_classes Defining base classes for your project]]
  * @todo Use Typed ActorSystem
  */
abstract class ActorSpecBase(system: ActorSystem)
    extends ScalaTestWithActorTestKit(system.toTyped)
    with AnyWordSpecLike
    with Matchers
    with Inside
    with BeforeAndAfterAll
    with Eventually
    with ScalaFutures
    with EitherValues
    with AkkaTypedSpanScaleFactorSupport
