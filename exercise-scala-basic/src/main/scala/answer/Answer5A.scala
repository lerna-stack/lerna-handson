package answer

object Answer5A extends App {

  def reciprocal(source: Vector[Int]): Vector[Double] = {
    source.map(n => {
      if (n == 0) {
        0
      } else {
        1.0 / n
      }
    })
  }

  // 1 ~ 10 までの要素に対して変換を実施する
  val source: Vector[Int] =
    Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
  val converted: Vector[Double] =
    reciprocal(source)

  // 結果を表示してみる
  converted.foreach { x: Double =>
    println(x)
  }

}
