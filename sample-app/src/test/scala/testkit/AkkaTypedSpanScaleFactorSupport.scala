package testkit

import akka.actor.typed.ActorSystem
import akka.testkit.TestKitExtension
import org.scalatest.concurrent.ScaledTimeSpans

/** A trait that provides time scale factor integration of ActorTestKit and ScalaTest
  *
  * ==Overview==
  * The trait can be used with [[akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit]].
  *
  * The time scale factor can be configured by `akka.test.timefactor` in a configuration file.
  */
trait AkkaTypedSpanScaleFactorSupport extends ScaledTimeSpans {
  implicit def system: ActorSystem[Nothing]

  /** Configure the ScalaTest time factor from Akka TestKit's test time factor.
    * <p>
    *  Configure the timefactor in your configuration file like this: `akka.test.timefactor = 3.0`
    * <p>
    * See also: <a href="http://doc.scalatest.org/3.0.1-2.12/org/scalatest/concurrent/ScaledTimeSpans.html">ScaledTimeSpans</a>
    */
  override def spanScaleFactor: Double =
    TestKitExtension(system).TestTimeFactor
}
