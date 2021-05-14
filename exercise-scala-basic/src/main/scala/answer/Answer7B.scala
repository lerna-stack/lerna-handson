package answer

object Answer7B extends App {

  def fizzbuzz(source: Vector[Int]): Vector[String] = {
    source.map(x =>
      x match {
        case _ if x % 15 == 0 =>
          "FizzBuzz"
        case _ if x % 5 == 0 =>
          "Buzz"
        case _ if x % 3 == 0 =>
          "Fizz"
        case _ =>
          x.toString
      },
    )
  }

  val source: Vector[Int]       = (1 to 100).toVector
  val converted: Vector[String] = fizzbuzz(source)
  converted.foreach(println)

}
