package exercise

import akka.actor.testkit.typed.scaladsl.{ ScalaTestWithActorTestKit, TestProbe }
import akka.actor.typed.ActorRef
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import testing.tags.ExerciseTest

/** 演習3: 実装した [[UpperCaseEchoActor]] をテストしてみよう。
  *  - 文字列を送信し、大文字に変換されて返信されるかテストする
  *  - `TestProbe.expectMessage`  が使える
  *
  *  解答は [[answer.Answer3]] で確認できる。
  */
@ExerciseTest
final class Exercise3 extends ScalaTestWithActorTestKit() with AnyWordSpecLike with Matchers {

  "UpperCaseEchoActor should echo back upper-case-message" in {
    val actorRef: ActorRef[UpperCaseEchoActor.Message] =
      spawn(UpperCaseEchoActor())
    val probe: TestProbe[String] =
      createTestProbe[String]()

    // ここにテストを記述しよう

  }

}
