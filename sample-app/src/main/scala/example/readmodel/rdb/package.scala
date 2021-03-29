package example.readmodel

import scala.concurrent.ExecutionContext

package object rdb {
  // レポジトリで使う ExecutionContext
  type RepositoryExecutionContext = ExecutionContext
}
