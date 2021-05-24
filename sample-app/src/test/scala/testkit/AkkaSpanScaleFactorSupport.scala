package testkit

import akka.actor.ActorSystem
import akka.testkit.TestKitExtension
import org.scalatest.concurrent.ScaledTimeSpans

/** A trait that provides time scale factor integration of Akka TestKit and ScalaTest
  *
  * ==Overview==
  * The trait can be used with [[akka.testkit.TestKit]] or other Akka classic test kits such as
  * [[https://doc.akka.io/api/akka-http/current/akka/http/scaladsl/testkit/ScalatestRouteTest.html ScalaTestRouteTest]]
  *
  * The time scale factor can be configured by `akka.test.timefactor` in a configuration file.
  */
trait AkkaSpanScaleFactorSupport extends ScaledTimeSpans {
  // No trait provides the ActorSystem in both TestKit and RouteTest
  implicit def system: ActorSystem

  /** Configure the ScalaTest time factor from Akka TestKit's test time factor.
    * <p>
    *  Configure the timefactor in your configuration file like this: `akka.test.timefactor = 3.0`
    * <p>
    * See also: <a href="http://doc.scalatest.org/3.0.1-2.12/org/scalatest/concurrent/ScaledTimeSpans.html">ScaledTimeSpans</a>
    */
  override def spanScaleFactor: Double =
    TestKitExtension(system).TestTimeFactor
}
