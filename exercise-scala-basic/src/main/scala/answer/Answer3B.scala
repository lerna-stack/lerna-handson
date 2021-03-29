package answer

object Answer3B extends App {
  // (B)
  // メソッド定義
  def doubling(obj: AnyVal): Unit = {
    obj match {
      case int: Int =>
        println(int * 2)
      case double: Double =>
        println(double * 2)
      case other =>
        println(other)
    }
  }

  // ↑のメソッドを呼び出している
  doubling(12)    // 24
  doubling(0.125) // 0.25
  doubling('c')   // c
}
