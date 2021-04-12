package example

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

// curl --silent --noproxy '*' localhost:8080/hello
object BasicExample extends App {
  private implicit val system           = ActorSystem("basic-example")
  private implicit val executionContext = system.dispatcher

  private val route: Route =
    path("hello") {
      get {
        complete("world")
      }
    }
  Http().newServerAt("localhost", 8080).bind(route)
}
