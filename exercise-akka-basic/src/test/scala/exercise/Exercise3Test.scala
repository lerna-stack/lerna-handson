package exercise

import akka.actor._
import akka.testkit.{ ImplicitSender, TestKit }
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

final class Exercise3Test
    extends TestKit(ActorSystem("exercise3"))
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll
    with ImplicitSender {
  override def afterAll(): Unit = {
    shutdown(system)
  }

  // ここに MyCounterActor のテストを書こう

}
