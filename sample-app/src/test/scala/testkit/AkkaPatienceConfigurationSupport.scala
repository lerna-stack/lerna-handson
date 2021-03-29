package testkit

import akka.actor.ActorSystem
import akka.testkit.TestKitExtension
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.time.Span

/** A trait that provides timeout setting integration of Akka Classic TestKit and ScalaTest
  *
  * ==Overview==
  * The trait can be used with [[akka.testkit.TestKit]] or other Akka classic test kits such as
  * [[https://doc.akka.io/api/akka-http/current/akka/http/scaladsl/testkit/ScalatestRouteTest.html ScalaTestRouteTest]]
  *
  * The timeout can be configured by `akka.test.default-timeout` in a configuration file.
  */
trait AkkaPatienceConfigurationSupport extends PatienceConfiguration {
  // No trait provides the ActorSystem in both TestKit and RouteTest
  implicit val system: ActorSystem

  /** `PatienceConfig` from [[akka.testkit.TestKitSettings.DefaultTimeout]]
    */
  implicit override lazy val patienceConfig: PatienceConfig = {
    val testKitSettings = TestKitExtension(system)
    PatienceConfig(scaled(testKitSettings.DefaultTimeout.duration), scaled(Span(100, org.scalatest.time.Millis)))
  }
}
