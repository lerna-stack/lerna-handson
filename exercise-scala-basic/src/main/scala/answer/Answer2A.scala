package answer

object Answer2A extends App {
  // (A)
  // メソッド定義
  def reciprocal(n: Int): Unit = {
    val r: Double = n match {
      case 0       => 0
      case nonZero => 1.0 / nonZero
    }
    println(r)
  }

  // ↑のメソッドを呼び出している
  reciprocal(0) // 0.0
  reciprocal(1) // 1.0
  reciprocal(2) // 0.5
  reciprocal(5) // 0.2
}
