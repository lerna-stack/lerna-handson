package answer

object Answer2B extends App {
  // (B)
  for (i <- 1 to 10) {
    if (i % 2 == 0) {
      println(i)
    }
  }
  // or
  for (i <- 2 to 10 by 2) {
    println(i)
  }
}
