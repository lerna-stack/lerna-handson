package answer

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

// curl --silent --noproxy '*' "localhost:8080/query-example?p=12"
object Answer2C extends App {
  private implicit val system = ActorSystem(Behaviors.empty, "entity-example")

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
