package example

object ObjectExample extends App {
  // シングルトンのように使えます。
  // メソッドも定義できます。
  // トレイトもミクスインできます。
  object MyObject {
    val limit: Int = 100
    def foo(): Unit = {
      println("bar")
    }
  }

  println(MyObject.limit) // 100
  MyObject.foo()          // bar
}
