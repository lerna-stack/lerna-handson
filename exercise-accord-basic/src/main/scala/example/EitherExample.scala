package example

object EitherExample extends App {
  // MyError もしくは 正常値(Int型) を表現できる。
  case class MyError()
  val left: Either[MyError, Int]  = Left(new MyError())
  val right: Either[MyError, Int] = Right(1)

  // パターンマッチで処理できる。
  left match {
    case Left(error) =>
      // handle the error
      println(s"got $error")
    case Right(value) =>
      // we are all happy!
      assert(false)
  }
}
