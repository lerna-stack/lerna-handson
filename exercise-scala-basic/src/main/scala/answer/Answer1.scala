package answer

object Answer1 extends App {
  // (A)
  val x: Int    = 123456
  val y: Double = 3.1415
  val z: String = "oops"

  // (B)
  println(x)
  println(y)
  println(z)

  // (C)
  if (x > 0) {
    println("positive")
  } else {
    println("zero or negative")
  }
}
