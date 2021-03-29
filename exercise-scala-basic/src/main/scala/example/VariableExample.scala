package example

object VariableExample extends App {
  // 再代入不可能(推奨)
  val x: Int = 1
  //  compile error
  // x = 2

  // 再代入可能
  var y: Int = 1
  y = 2
}
