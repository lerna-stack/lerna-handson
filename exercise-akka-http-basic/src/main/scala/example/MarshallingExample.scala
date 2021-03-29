package example

import akka.http.scaladsl.server.Directives._

object MarshallingExample extends App {
  // spray-json を使った基本的なマーシャリングを有効にしている
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import spray.json.DefaultJsonProtocol._

  // Vector など基本的な型はJSONでマーシャンリグできる
  // こういうJSON """[ 1, 2, 3 ]""" を Vector(1,2,3) に変換できる.
  entity(as[Vector[Int]])
  // Vector(1,2,3) が """[ 1, 2, 3 ]"""
  complete(Vector(1, 2, 3))

  // マーシャリングのカスタム定義を書くことで
  // 自身で定義したクラスもマーシャリングできる。
  // 今回使うサンプルでは事前に定義している。
  private case class MyCustomClass(name: String, age: Int)
  private implicit val requestBodyFormat = jsonFormat2(MyCustomClass)
  // こういう JSON を """{ "name": "my-name", "age": 123 }"""
  // MyCustomClass("my-name", 123) に変換できる。
  entity(as[MyCustomClass])
  // 逆もできる
  // MyCustomClass("my-name", 123) が
  // """{ "name": "my-name", "age": 123 }""" となる
  complete(MyCustomClass("my-name", 123))
}
