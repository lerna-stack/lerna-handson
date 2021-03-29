package answer

object Answer2C extends App {
  // (C)
  var sum1: Int = 0
  for (i <- 1 to 10) {
    sum1 += i
  }
  println(sum1)
  // or
  val sum2: Int = (1 to 10).sum
  println(sum2)
}
