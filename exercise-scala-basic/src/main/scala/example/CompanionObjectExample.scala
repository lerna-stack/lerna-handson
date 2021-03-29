package example

object CompanionObjectExample extends App {
  // 同じ名前のclass(traitもOK)とobjectを定義することができます。
  // このときの object はコンパニオンオブジェクトと呼ばれます。
  // javaでいう static変数やメソッドの役割を担います。
  object MyClass {
    // プライベートで宣言しても、
    private val value: Int = 100
  }

  class MyClass {
    def print(): Unit = {
      // 参照できます :-)
      println(MyClass.value)
    }
    def print2(): Unit = {
      // 参考: 次のようにインポートすることもできます。
      import MyClass._
      println(value)
    }
  }

  val obj: MyClass = new MyClass()
  obj.print()  // 100
  obj.print2() // 100
}
