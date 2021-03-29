package testkit

import org.scalatest.{ BeforeAndAfterAll, Suite }
import wvlet.airframe.{ Design, Session }

trait AirframeDiSessionSupport extends BeforeAndAfterAll { this: Suite =>
  protected val design: Design
  protected lazy val session: Session = design.newSession

  override protected def afterAll(): Unit = {
    try super.afterAll()
    finally session.shutdown
  }
}
