package example

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route

// curl --silent --noproxy '*' localhost:8080/example/123
object PathMatcherExample extends App {
  private implicit val system = ActorSystem(Behaviors.empty, "path-matcher-example")

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
  Http().newServerAt("localhost", 8080).bind(route)
}
