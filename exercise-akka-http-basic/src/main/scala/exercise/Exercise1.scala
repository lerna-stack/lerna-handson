package exercise

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

/*
- GET /my/hello を実装してみよう
  my_world を 文字列で返そう
- 実行して、curlコマンドで確認しよう
  curl --silent --noproxy '*' localhost:8080/my/hello
- 他のパスも定義してみよう
  GET /my/question
  返す文字列は何でもよい
  2つ以上のパスを定義するには concat(route1, route2) が使える

   curl --silent --noproxy '*' localhost:8080/my/hello
   curl --silent --noproxy '*' localhost:8080/my/question
 */
object Exercise1 extends App {
  private implicit val system = ActorSystem("exercise1")

  private val route: Route = {
    ???
  }
  Http().newServerAt("localhost", 8080).bind(route)
}
