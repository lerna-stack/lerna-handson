package example

object AbstractClassExample extends App {
  // 抽象クラスは abstract で宣言できる
  abstract class Animal() {
    // 内容を定義していないメソッド
    // (メソッドにabstractはつけない)
    def bark(): Unit
  }

  // extends を使って 継承する
  class Dog() extends Animal {
    override def bark(): Unit = {
      println("Dog")
    }
  }

  // abstract クラスで継承できる
  abstract class PerfectHuman() extends Animal {
    def dance(): Unit
  }

  // final を使って Catクラスの継承を禁止する
  final class Cat() extends Animal {
    override def bark(): Unit = {
      println("Cat")
    }
  }

  val animal: Animal = new Dog()
  animal.bark() // Dog

  val animal2: Animal = new Cat()
  animal2.bark() // Cat
}
