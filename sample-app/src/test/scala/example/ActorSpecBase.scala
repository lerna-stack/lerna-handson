package example

import akka.actor.testkit.typed.scaladsl.{ ActorTestKit, ScalaTestWithActorTestKit }
import com.typesafe.config.{ Config, ConfigFactory }
import org.scalatest.concurrent.{ Eventually, ScalaFutures }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{ BeforeAndAfterAll, EitherValues, Inside }
import testkit.AkkaTypedSpanScaleFactorSupport

/** A test class which improve consistency and reduce boilerplate.
  *
  * @see [[https://www.scalatest.org/user_guide/defining_base_classes Defining base classes for your project]]
  */
abstract class ActorSpecBase(testKit: ActorTestKit)
    extends ScalaTestWithActorTestKit(testKit)
    with AnyWordSpecLike
    with Matchers
    with Inside
    with BeforeAndAfterAll
    with Eventually
    with ScalaFutures
    with EitherValues
    with AkkaTypedSpanScaleFactorSupport {

  def this() = {
    // デフォルトの振る舞いでは `application-test` もしくは `reference` のみが読み込まれる。
    // テスト全般にわたって `application` と `reference` を使用したいため、ConfigFactory#load を使用する。
    this(ActorTestKit(ConfigFactory.load()))
  }

  def this(customConfig: Config) = {
    this(ActorTestKit(customConfig))
  }

}
