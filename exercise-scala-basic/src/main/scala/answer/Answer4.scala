package answer

object Answer4 extends App {
  // using if else expression
  for (x <- 1 to 100) {
    val divisibleBy3: Boolean = x % 3 == 0
    val divisibleBy5: Boolean = x % 5 == 0
    val msg: String = if (divisibleBy3 && divisibleBy5) {
      "FizzBuzz"
    } else if (divisibleBy3) {
      "Fizz"
    } else if (divisibleBy5) {
      "Buzz"
    } else {
      x.toString
    }
    println(msg)
  }

  // or using match expression
  for (x <- 1 to 100) {
    val msg: String = x match {
      case x15 if x % 15 == 0 =>
        "FizzBuzz"
      case x5 if x % 5 == 0 =>
        "Buzz"
      case x3 if x % 3 == 0 =>
        "Fizz"
      case _ =>
        x.toString
    }
    println(msg)
  }
}
