package answer

object Answer7A extends App {
  def reciprocal(source: Vector[Int]): Vector[Double] = {
    source.map(n => {
      if (n == 0) {
        0
      } else {
        1.0 / n
      }
    })
  }

  val source: Vector[Int]       = (1 to 10).toVector
  val converted: Vector[Double] = reciprocal(source)
  for (x <- converted) {
    println(x)
  }
}
