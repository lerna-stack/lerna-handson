package example

object ObjectApplyExample extends App {

  object SquaredIntFactory {
    // apply メソッドは特別な意味を持つ
    def apply(value: Int): Int = {
      value * value
    }
  }

  // 関数のように振る舞う
  val number: Int = SquaredIntFactory(2)
  println(number) // 4

}
