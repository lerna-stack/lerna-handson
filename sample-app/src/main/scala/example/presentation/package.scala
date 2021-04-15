package example

import scala.concurrent.ExecutionContext

package object presentation {
  // Application で使う ExecutionContext
  type PresentationExecutionContext = ExecutionContext
}
