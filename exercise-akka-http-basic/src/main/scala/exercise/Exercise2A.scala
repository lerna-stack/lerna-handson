package exercise

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Route

/*
- GET /path-example/{int}
  - Path Matcher を使おう
  - {int} を 2倍にして文字列で返そう
  - curl --silent --noproxy '*' localhost:8080/path-example/1234
 */
object Exercise2A extends App {
  private implicit val system           = ActorSystem("exercise2a")
  private implicit val executionContext = system.dispatcher

  private val route: Route = {
    ???
  }
  Http().bindAndHandle(route, "localhost", 8080)

}
