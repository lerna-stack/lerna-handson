package exercise

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._

/*
- POST /body-example
  リクエストボディを MyRequestBody型で取り出そう
  MyResponseBody形に変換して返そう。型は定義してある。
  レスポンスの構築方法
    - message フィールドは、文字列"hello"を設定する。
    - value フィールドは、リクエストで受け取った value の2倍を設定する。

  curl --silent --noproxy '*' -X POST  -H "Content-Type:application/json" -d '{"value":123}' localhost:8080/body-example
 */
object Exercise2B extends App {
  private implicit val system           = ActorSystem("answer2b")
  private implicit val executionContext = system.dispatcher

  // リクエストとレスポンスの型
  private case class MyRequestBody(value: Int)
  private case class MyResponseBody(message: String, value: Int)

  // JSONに変換するためのマーシャラ定義(裏側で勝手に使われる,おまじないだと思ってね)
  private implicit val myRequestBodyFormat  = jsonFormat1(MyRequestBody)
  private implicit val myResponseBodyFormat = jsonFormat2(MyResponseBody)

  // ここに答えを書く
  private val route: Route = {
    ???
  }
  Http().newServerAt("localhost", 8080).bind(route)
}
