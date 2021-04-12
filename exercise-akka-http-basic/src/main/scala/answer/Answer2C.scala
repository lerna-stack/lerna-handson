package answer

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

// curl --silent --noproxy '*' "localhost:8080/query-example?p=12"
object Answer2C extends App {
  private implicit val system           = ActorSystem("entity-example")
  private implicit val executionContext = system.dispatcher

  private val route: Route = {
    path("query-example") {
      get {
        parameters("p".as[Int]) { p =>
          complete((p * 2).toString)
        }
      }
    }
  }
  Http().newServerAt("localhost", 8080).bind(route)
}
