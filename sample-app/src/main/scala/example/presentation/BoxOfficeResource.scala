package example.presentation

import akka.http.scaladsl.server.Route

/** API サーバが使うRouteを提供する。
  */
trait BoxOfficeResource {
  def routes: Route
}
