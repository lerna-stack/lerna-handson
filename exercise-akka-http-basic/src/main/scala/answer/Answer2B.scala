package answer

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._

// curl --silent --noproxy '*' -X POST  -H "Content-Type:application/json" -d '{"value":123}' localhost:8080/body-example
object Answer2B extends App {
  private implicit val system           = ActorSystem("answer2b")
  private implicit val executionContext = system.dispatcher

  private case class MyRequestBody(value: Int)
  private case class MyResponseBody(message: String, value: Int)

  private implicit val myRequestBodyFormat  = jsonFormat1(MyRequestBody)
  private implicit val myResponseBodyFormat = jsonFormat2(MyResponseBody)

  private val route: Route =
    path("body-example") {
      post {
        entity(as[MyRequestBody]) { requestBody =>
          val responseBody = MyResponseBody("hello", requestBody.value * 2)
          complete(responseBody)
        }
      }
    }
  Http().bindAndHandle(route, "localhost", 8080)
}
