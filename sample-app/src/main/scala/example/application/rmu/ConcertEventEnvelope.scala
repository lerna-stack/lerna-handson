package example.application.rmu

import akka.persistence.query.Offset
import example.model.concert.ConcertEvent

case class ConcertEventEnvelope(offset: Offset, event: ConcertEvent)
