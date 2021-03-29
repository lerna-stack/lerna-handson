package example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

// curl --silent --noproxy '*' localhost:8080/example/hello
object PathExample extends App {
  private implicit val system           = ActorSystem("path-example")
  private implicit val executionContext = system.dispatcher

  private val route: Route =
    path("example" / "hello") {
      get {
        complete("example_world")
      }
    }
  Http().bindAndHandle(route, "localhost", 8080)
}
