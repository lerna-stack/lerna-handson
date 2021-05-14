package example.application.command

import akka.actor.typed.ActorSystem
import example.ActorSpecBase
import example.adapter.{ ConcertIdGenerator, ConcertTicketId }
import example.adapter.command.BoxOfficeService
import example.application.ApplicationDIDesign
import testkit.AirframeDiSessionSupport
import wvlet.airframe.Design

final class DefaultBoxOfficeServiceSpec
    extends ActorSpecBase()
    with ClusterShardingSpecLike
    with AirframeDiSessionSupport {

  import example.adapter.command.BoxOfficeService._

  override protected val design: Design =
    ApplicationDIDesign.design
      .bind[ActorSystem[Nothing]].toInstance(system)

  val service: DefaultBoxOfficeService =
    session.build[DefaultBoxOfficeService]
  val idGenerator = new ConcertIdGenerator()

  "create a concert" in {
    val id             = idGenerator.nextId()
    val responseFuture = service.createConcert(id, 100)
    responseFuture.futureValue shouldBe CreateConcertResponse(100)
  }

  "get the concert" in {
    val id                   = idGenerator.nextId()
    val createResponseFuture = service.createConcert(id, 10)
    createResponseFuture.futureValue shouldBe a[CreateConcertResponse]

    val getResponseFuture = service.getConcert(id)
    getResponseFuture.futureValue shouldBe
    GetConcertResponse((1 to 10).map(ConcertTicketId).toVector, cancelled = false)
  }

  "buy concert tickets" in {
    val id                   = idGenerator.nextId()
    val createResponseFuture = service.createConcert(id, 10)
    createResponseFuture.futureValue shouldBe a[CreateConcertResponse]

    val buyResponseFuture = service.buyConcertTickets(id, 3)
    buyResponseFuture.futureValue shouldBe
    BuyConcertTicketsResponse((1 to 3).map(ConcertTicketId).toVector)
  }

  "cancel the concert" in {
    val id                   = idGenerator.nextId()
    val createResponseFuture = service.createConcert(id, 10)
    createResponseFuture.futureValue shouldBe a[CreateConcertResponse]

    val cancelResponseFuture = service.cancelConcert(id)
    cancelResponseFuture.futureValue shouldBe CancelConcertResponse(10)
  }

}
