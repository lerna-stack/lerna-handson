package example

object TraitExample extends App {
  // トレイトの定義
  trait Animal {
    // デフォルト実装が書ける
    def bark(): Unit = {
      println("bark")
    }
  }
  trait Danceable {
    def dance(): Unit
  }

  // 複数個のトレイトをミクスインできる
  final class Dog extends Animal with Danceable {
    override def dance(): Unit = {
      println("dance")
    }
  }

  val animal: Animal = new Dog()
  animal.bark() // bark

  val danceable: Danceable = new Dog()
  danceable.dance() // dance
}
