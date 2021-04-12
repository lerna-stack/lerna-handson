package example

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._
import spray.json._

// curl --silent --noproxy '*' "localhost:8080/example/query?s=hello&page=3"
object QueryParameterExample extends App {
  private implicit val system           = ActorSystem("query-parameter-example")
  private implicit val executionContext = system.dispatcher

  private val route: Route = {
    // /example/query?s=hi&page=10
    // クエリパラメタを処理する
    // JSON で返す。
    path("example" / "query") {
      get {
        parameters("s", "page".as[Int] ? 0) { (searchString, page) =>
          complete((searchString, page).toJson)
        }
      }
    }
  }
  Http().newServerAt("localhost", 8080).bind(route)
}
