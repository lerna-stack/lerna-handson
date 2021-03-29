package example

object MatchExample extends App {
  // 文字列、値を使う
  val str: String = "abc"
  val newVal: String = str match {
    case "abc" => "alpha"
    case "123" => "num"
    case _     => "???"
  }
  println(newVal)

  // 型を使う
  val obj: AnyRef = "abc"
  obj match {
    case value: Integer =>
      println(s"$value is Integer")
    case value: String =>
      println(s"$value is String")
    case value =>
      println(s"$value is something else")
  }
}
