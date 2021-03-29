package example.model.concert

import java.util.concurrent.atomic.AtomicInteger

// ConcertId 生成器 (テストで使用する)
// フォーマット: id_{seq_int}
final class ConcertIdGenerator {
  // NOTE: シーケンシャルに採番するのでこのユニットテスト内ではIDは衝突しない。
  // NOTE: テストケースを並列実行する場合があるため AtomicIntegerを使っている。
  private val nextSeqId = new AtomicInteger(1)
  def nextId(): ConcertId = {
    val seqId = nextSeqId.getAndIncrement()
    ConcertId
      .fromString(s"id_${seqId}")
      .left.map(e => new IllegalStateException(e.toString))
      .toTry.get
  }
}
