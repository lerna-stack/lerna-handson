package answer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

// curl --silent --noproxy '*' localhost:8080/my/hello
// curl --silent --noproxy '*' localhost:8080/my/question
object Answer1 extends App {
  private implicit val system           = ActorSystem("answer1")
  private implicit val executionContext = system.dispatcher

  private val route: Route = concat(
    path("my" / "hello") {
      get {
        complete("my_world")
      }
    },
    path("my" / "question") {
      get {
        complete("my-answer")
      }
    },
  )
  Http().bindAndHandle(route, "localhost", 8080)
}
