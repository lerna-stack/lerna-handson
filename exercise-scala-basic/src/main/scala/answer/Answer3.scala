package answer

object Answer3 extends App {
  class Circle(val x: Int, val y: Int, val r: Int) {
    def area(): Double = math.Pi * r * r

    // Scalaで推奨されている方法
    // 副作用を伴わない getter に近いメソッドは 定義時に () を省略することで、
    // プロパティのようにアクセスできる。
    def areaScalike: Double = math.Pi * r * r
  }

  val myCircle: Circle = new Circle(1, 2, 10)
  println(myCircle.x)
  println(myCircle.y)
  println(myCircle.r)
  println(myCircle.area())
  println(myCircle.areaScalike)
}
