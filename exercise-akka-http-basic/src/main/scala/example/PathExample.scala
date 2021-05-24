package example

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

// curl --silent --noproxy '*' localhost:8080/example/hello
object PathExample extends App {
  private implicit val system = ActorSystem(Behaviors.empty, "path-example")

  private val route: Route =
    path("example" / "hello") {
      get {
        complete("example_world")
      }
    }
  Http().newServerAt("localhost", 8080).bind(route)
}
