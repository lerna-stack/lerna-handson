package example.serialization

import akka.persistence.journal.{ Tagged, WriteEventAdapter }
import example.model.concert.ConcertEvent

final class ConcertEventAdapter extends WriteEventAdapter {
  override def manifest(event: Any): String = ""
  override def toJournal(event: Any): Any = {
    // ConcertEventのみを処理する
    // 設定ミス等で間違ったイベントにタグをつけないように型チェックをしておく
    event match {
      case _: ConcertEvent =>
        Tagged(event, Set(ConcertEvent.tag))
      case _ =>
        event
    }
  }
}
