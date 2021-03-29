package example.application.rmu

import akka.NotUsed
import akka.persistence.query.Offset
import akka.stream.scaladsl.Source

/** ConcertEventのストリームを生成するファクトリ
  */
trait ConcertEventSourceFactory {

  /** オフセット以降(含まない)に発生したConcertEventのストリームを生成する。
    * @param offset オフセット
    * @return ストリーム
    */
  def createEventStream(offset: Offset): Source[ConcertEventEnvelope, NotUsed]
}
