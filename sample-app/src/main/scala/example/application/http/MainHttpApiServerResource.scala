package example.application.http

import akka.http.scaladsl.server.Route

/** API サーバが使うRouteを提供する。
  */
trait MainHttpApiServerResource {
  def routes: Route
}
