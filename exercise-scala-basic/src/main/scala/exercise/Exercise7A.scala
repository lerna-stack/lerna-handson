package exercise

object Exercise7A extends App {
  /*

   - Vector[Int] を受け取り、各要素Nについて、次のように変換するメソッドを実装しよう
      - 0以外なら0
      - 0以外なら1.0/N

   - foreach とはなんだろう？
     https://www.scala-lang.org/api/2.13.5/scala/collection/immutable/Vector.html で調べてみよう。

   */

  def reciprocal(source: Vector[Int]): Vector[Double] = {
    // ??? を消して、ここに変換するコードを書こう
    ???
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
