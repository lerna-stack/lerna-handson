package example

object ClassExample extends App {
  // クラス定義
  // - Int型のメンバ x と y を持つ
  // - メンバ x, y は不変で公開されている
  // - x,y を引数とする
  class Point(val x: Int, val y: Int) {
    // 他のメンバを定義することもできる
    // デフォルトは public である
    val z: Int = x + y
    // 修飾子をつけることもできる
    protected var w: Double = 0

    // メソッド定義
    // Int型引数dxを受け取る
    // 戻り値は Point 型である
    def movedLeft(dx: Int): Point = {
      // クラスのインスタンスを作る場合は
      // new クラス名とする
      new Point(x + dx, y);
    }
  }

  val myPoint: Point = new Point(2, 1)
  // x と y と z は アクセス可能、 wは不可能
  println(myPoint.x) // 2
  println(myPoint.y) // 1
  println(myPoint.z) // 3

  // java と同様にメソッド呼び出しができる
  val myPoint3: Point = myPoint.movedLeft(100)
}
