package example

object IfExample2 extends App {
  val x: Int = 2

  // 式なので評価したら値になる
  val y: Double = if (x == 0) {
    x
  } else {
    1.0 / x
  }

  // y == 0.5
  println(y)
}
