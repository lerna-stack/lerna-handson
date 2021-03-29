package exercise

object Exercise6 extends App {
  /*
    sealed trait Animal と それを実装した case class が定義されている。
    - パターンマッチを使い、各ケースクラスごとに以下の処理を試してみよう。
      Dog なら “dog” とコンソールに表示
      Cat なら “cat” とコンソールに表示
      Monkey なら “monkey” とコンソールに表示
    - Animal の実装として case class Lion を追加しよう
    - パターンマッチ部分を追加してみよう
      Lion なら “lion” とコンソールに表示
   */

  sealed trait Animal
  case class Dog()    extends Animal
  case class Cat()    extends Animal
  case class Monkey() extends Animal

  def print(animal: Animal): Unit = {
    // animal の種類ごとに 文字列をコンソールに表示しよう
  }

  print(Dog())
  print(Cat())
  print(Monkey())
}
