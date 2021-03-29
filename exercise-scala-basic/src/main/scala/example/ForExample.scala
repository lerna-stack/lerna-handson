package example

object ForExample extends App {
  // 1 ~ 5 を表示する
  for (x <- 1 to 5) {
    println(x)
  }

  // 0 ~ 4 を表示する
  for (x <- 0 until 5) {
    println(x)
  }

  // 1 ~ 10 を表示する
  val range1to10: Range = 1 to 10
  for (x <- range1to10) {
    println(x)
  }

  // コレクションを使う
  val xs: Vector[Int] = Vector(1, 2, 3, 4, 5)
  for (x <- xs) {
    println(x)
  }
}
