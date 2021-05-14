package exercise

object Exercise7B extends App {
  /*
    Vector[Int] を受け取り、FizzBuzzの結果を Vector[String] で返すメソッドを実装しよう
   */

  def fizzbuzz(source: Vector[Int]): Vector[String] = {
    ???
  }

  val source: Vector[Int]       = (1 to 100).toVector
  val converted: Vector[String] = fizzbuzz(source)
  converted.foreach(println)

}
