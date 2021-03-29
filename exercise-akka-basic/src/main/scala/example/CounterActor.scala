package example

import akka.actor._

final class CounterActor extends Actor {
  // カウンタ値 (通常の変数でよい)
  // ただし 安全のため private にすること
  private var counter: Int = 0

  // メソッドも定義できる
  // この アクター外部 (サブクラス以外) からメソッドを直接呼び出すことは、
  // 安全性のためしない(スレッドセーフなどが保証できなくなり、アクターモデルの旨味がなくなる)
  private def increment(delta: Int): Unit = {
    counter += delta
  }

  override def receive: Receive = {
    case msg: Any =>
      // メソッド呼び出ししても問題ない
      increment(+1)
      // counterのコピーを sender() に送る。
      // mutable.Vector などの変更できるものを返してはいけない。
      // immutable.Vector は変更できないので返してもよい
      sender() ! counter
  }
}
