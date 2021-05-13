package exercise

object Exercise2B extends App {
  /*
    あるAnyVal型の変数obj が与えられるので、
    Int, Double なら 2倍の値をコンソールに表示しよう。
    それ以外ならそのまま表示しよう。
   */

  // メソッド定義
  def doubling(obj: AnyVal): Unit = {
    // 解答はここに書こう
  }

  // ↑のメソッドを呼び出している
  doubling(12)    // 24
  doubling(0.125) // 0.25
  doubling('c')   // c
}
