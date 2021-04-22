package example

import scala.concurrent.ExecutionContext

package object presentation {
  // Presentation で使う ExecutionContext
  type PresentationExecutionContext = ExecutionContext
}
