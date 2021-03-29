package example

object PatternMatchAdvancedExample extends App {
  // sealed trait や sealed abstract class は、
  // 同じファイル内でのみ ミクスイン or 実装 できる
  sealed trait Animal
  case class Dog()    extends Animal
  case class Cat()    extends Animal
  case class Monkey() extends Animal

  // パターンマッチからMonkeyを消すと、
  // すべてのパターンを網羅していないため、コンパイラが警告を出す。
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
