package example

object PatternMatchAdvancedExample extends App {
  // sealed trait は、同じファイル内でのみ 実装 できる
  sealed trait Animal
  final case class Dog()    extends Animal
  final case class Cat()    extends Animal
  final case class Monkey() extends Animal

  // 次のパターンマッチから Monkey を消すと、
  // すべてのパターンを網羅できていないため、
  // コンパイラが警告を出すことを確認できる
  val animal: Animal = Cat()
  val msg: String = animal match {
    case dog: Dog =>
      dog.toString
    case cat: Cat =>
      cat.toString
    case monkey: Monkey =>
      monkey.toString
  }
  println(msg) // Cat()
}
