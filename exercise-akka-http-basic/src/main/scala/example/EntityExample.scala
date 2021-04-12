package example

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._

// curl --silent --noproxy '*' -X POST  -H "Content-Type:application/json" -d '{"value":123}' localhost:8080/example/entity
object EntityExample extends App {
  private implicit val system = ActorSystem("entity-example")

  private val route: Route = {
    // リクエストボディを取り出すためのケースクラス
    case class RequestBody(value: Int)
    implicit val requestBodyFormat = jsonFormat1(RequestBody)
    // /example/entity
    // リクエストボディを JSON として取り出す
    // "{ value: 123 }"
    path("example" / "entity") {
      post {
        entity(as[RequestBody]) { body =>
          // 2倍にして, 文字列で返す.
          val response = body.value * 2
          complete(response.toString)
        }
      }
    }
  }
  Http().newServerAt("localhost", 8080).bind(route)
}
