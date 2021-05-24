package example

import scala.concurrent.ExecutionContext

package object application {
  // Application で使う ExecutionContext
  type ApplicationExecutionContext = ExecutionContext
}
