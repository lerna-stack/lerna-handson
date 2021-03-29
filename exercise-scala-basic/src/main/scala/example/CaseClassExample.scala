package example

object CaseClassExample extends App {
  case class Point(x: Int, y: Int)

  // case class をインスタンス化するときは new をつけない
  val pointA: Point = Point(1, 2)
  val pointB: Point = Point(1, 2)

  // 自動実装されている
  println(pointA.toString) // Point(1,2)
  println(pointB.toString) // Point(1,2)

  println(pointA == pointB)      // true
  println(pointA != pointB)      // false
  println(pointA.equals(pointB)) // true
}
