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
  import example.model.concert.service.BoxOfficeService._

  val idGenerator = new ConcertIdGenerator()

  def boxOfficeService(newService: => BoxOfficeService): Unit = {

    "create a concert" in {
      val service        = newService
      val id             = idGenerator.nextId()
      val responseFuture = service.createConcert(id, 100)
      responseFuture.futureValue shouldBe CreateConcertResponse(100)
    }

    "get the concert" in {
      val service              = newService
      val id                   = idGenerator.nextId()
      val createResponseFuture = service.createConcert(id, 10)
      createResponseFuture.futureValue shouldBe a[CreateConcertResponse]

      val getResponseFuture = service.getConcert(id)
      getResponseFuture.futureValue shouldBe
      GetConcertResponse((1 to 10).map(ConcertTicketId).toVector, cancelled = false)
    }

    "buy concert tickets" in {
      val service              = newService
      val id                   = idGenerator.nextId()
      val createResponseFuture = service.createConcert(id, 10)
      createResponseFuture.futureValue shouldBe a[CreateConcertResponse]

      val buyResponseFuture = service.buyConcertTickets(id, 3)
      buyResponseFuture.futureValue shouldBe
      BuyConcertTicketsResponse((1 to 3).map(ConcertTicketId).toVector)
    }

    "cancel the concert" in {
      val service              = newService
      val id                   = idGenerator.nextId()
      val createResponseFuture = service.createConcert(id, 10)
      createResponseFuture.futureValue shouldBe a[CreateConcertResponse]

      val cancelResponseFuture = service.cancelConcert(id)
      cancelResponseFuture.futureValue shouldBe CancelConcertResponse(10)
    }

  }

}
