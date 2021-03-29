package example

object VectorExample extends App {
  // Int型を要素とするVector
  val myVector: Vector[Int] = Vector(1, 2, 3)
  // String型を要素とするVector
  val myStringVector: Vector[String] = Vector("a", "b", "c")

  // 型推論されます
  val myVector2                  = Vector(1, 2, 3)
  val myEmptyVector: Vector[Int] = Vector.empty

  // for で使うことができます
  for (x <- Vector(1, 2, 3)) {
    println(x)
  }

  // 要素を2倍する
  // = Vector(2, 4, 6)
  val myNewVector1: Vector[Int] =
    Vector(1, 2, 3).map(e => e * 2)

  // 要素を2倍する
  // = Vector(2, 4, 6)
  val myNewVector2: Vector[Int] =
    Vector(1, 2, 3).map(_ * 2)

  // 要素を2倍する
  // = Vector(2, 4, 6)
  val doubling: Int => Int = _ * 2
  val myNewVector3: Vector[Int] =
    Vector(1, 2, 3).map(doubling)

  // 要素を文字列に変換する
  // = Vector("1","2","3")
  val myNewStringVector1: Vector[String] =
    Vector(1, 2, 3).map(e => e.toString)

  // 要素を文字列に変換する
  // = Vector("1","2","3")
  val myNewStringVector2: Vector[String] =
    Vector(1, 2, 3).map(_.toString)

}
