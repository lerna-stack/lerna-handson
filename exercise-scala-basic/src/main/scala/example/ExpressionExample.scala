package example

import scala.annotation.nowarn

// "式" のサンプルで警告がでるため
@nowarn("cat=other-pure-statement")
object ExpressionExample extends App {
  // 式
  1 + 1

  // 文
  val x: Int = 1

  // ブロック式(と便宜上呼ぶ)
  val y: Int = {
    val z: Int = 1
    z + 1
  }
  // y == 2
  println(y)
}
