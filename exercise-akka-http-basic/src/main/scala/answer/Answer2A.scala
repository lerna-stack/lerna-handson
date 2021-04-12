package answer

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

// curl --silent --noproxy '*' localhost:8080/path-example/1234
object Answer2A extends App {
  private implicit val system = ActorSystem(Behaviors.empty, "answer2a")

  private val route: Route =
    path("path-example" / IntNumber) { value: Int =>
      get {
        complete((value * 2).toString)
      }
    }
  Http().newServerAt("localhost", 8080).bind(route)
}
