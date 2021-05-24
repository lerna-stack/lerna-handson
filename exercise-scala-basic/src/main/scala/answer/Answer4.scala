package answer

object Answer4 extends App {
  sealed trait Animal
  case class Dog()    extends Animal
  case class Cat()    extends Animal
  case class Monkey() extends Animal
  case class Lion()   extends Animal

  def print(animal: Animal): Unit = {
    // animal の種類ごとに 文字列をコンソールに表示しよう
    val msg = animal match {
      case dog: Dog =>
        "dog"
      case cat: Cat =>
        "cat"
      case monkey: Monkey =>
        "monkey"
      case lion: Lion =>
        "lion"
    }
    println(msg)
  }

  print(Dog())
  print(Cat())
  print(Monkey())
  print(Lion())
}
