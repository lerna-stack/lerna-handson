package example

object IfExample extends App {
  val x: Int = 1
  if (x == 0) {
    println("x is zero")
  } else {
    // else は 必要なければ書かなくてよい
    println("x is not zero")
  }
}
