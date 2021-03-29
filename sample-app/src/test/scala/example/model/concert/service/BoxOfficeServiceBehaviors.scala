package example.model.concert.service

import example.model.concert.{ ConcertIdGenerator, ConcertTicketId }

/** [[BoxOfficeService]] の 共通テスト を定義する
  *
  * テスト共通化のため、このような形式でテストケースを実装する。
  *
  *  テスト共通化の方法については、
  * [[https://www.scalatest.org/user_guide/sharing_tests Sharing tests]]
  * を参照すること
  */
trait BoxOfficeServiceBehaviors { this: BoxOfficeServiceSpecBase =>
  import example.model.concert.actor.ConcertActorProtocol._

  val idGenerator = new ConcertIdGenerator()

  def boxOfficeService(newService: => BoxOfficeService): Unit = {

    "create a concert" in {
      val service        = newService
      val id             = idGenerator.nextId()
      val request        = CreateConcertRequest(id, 100)
      val responseFuture = service.createConcert(request)
      responseFuture.futureValue shouldBe CreateConcertSucceeded(100)
    }

    "get the concert" in {
      val service              = newService
      val id                   = idGenerator.nextId()
      val createResponseFuture = service.createConcert(CreateConcertRequest(id, 10))
      createResponseFuture.futureValue.isInstanceOf[CreateConcertSucceeded] shouldBe true

      val getResponseFuture = service.getConcert(GetConcertRequest(id))
      getResponseFuture.futureValue shouldBe
      GetConcertSucceeded(id, (1 to 10).map(ConcertTicketId).toVector, cancelled = false)
    }

    "buy concert tickets" in {
      val service              = newService
      val id                   = idGenerator.nextId()
      val createResponseFuture = service.createConcert(CreateConcertRequest(id, 10))
      createResponseFuture.futureValue.isInstanceOf[CreateConcertSucceeded] shouldBe true

      val buyResponseFuture = service.buyConcertTickets(BuyConcertTicketsRequest(id, 3))
      buyResponseFuture.futureValue shouldBe
      BuyConcertTicketsSucceeded((1 to 3).map(ConcertTicketId).toVector)
    }

    "cancel the concert" in {
      val service              = newService
      val id                   = idGenerator.nextId()
      val createResponseFuture = service.createConcert(CreateConcertRequest(id, 10))
      createResponseFuture.futureValue.isInstanceOf[CreateConcertSucceeded] shouldBe true

      val cancelResponseFuture = service.cancelConcert(CancelConcertRequest(id))
      cancelResponseFuture.futureValue shouldBe CancelConcertSucceeded(10)
    }

  }

}
