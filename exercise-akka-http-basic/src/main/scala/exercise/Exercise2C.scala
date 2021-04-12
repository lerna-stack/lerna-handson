package exercise

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._

/*
- GET /query-example?p={int}
  クエリパラメータ(必須)から int型整数を取り出し、2倍にして文字列で返そう

  curl --silent --noproxy '*' "localhost:8080/query-example?p=12"
 */
object Exercise2C extends App {
  private implicit val system = ActorSystem(Behaviors.empty, "entity-example")

  private val route: Route = {
    ???
  }
  Http().newServerAt("localhost", 8080).bind(route)
}
