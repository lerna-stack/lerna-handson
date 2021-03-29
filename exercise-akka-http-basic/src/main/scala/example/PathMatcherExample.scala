package example

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route

// curl --silent --noproxy '*' localhost:8080/example/123
object PathMatcherExample extends App {
  private implicit val system           = ActorSystem("path-matcher-example")
  private implicit val executionContext = system.dispatcher

  private val route: Route =
    // /example/{IntNumber} のパスを定義する。
    path("example" / IntNumber) { value: Int =>
      get {
        // { IntNumber }の部分を引数として受け取れる。
        // 2倍にして,文字列で返す.
        val response = value * 2
        complete(response.toString)
      }
    }
  Http().bindAndHandle(route, "localhost", 8080)
}
