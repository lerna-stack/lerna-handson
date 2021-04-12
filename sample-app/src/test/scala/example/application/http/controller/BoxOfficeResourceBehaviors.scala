package example.application.http.controller

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, StatusCodes }
import example.RouteSpecBase
import example.application.http.MainHttpApiServerResource
import example.model.concert.service.BoxOfficeService
import example.model.concert.{ ConcertIdGenerator, ConcertTicketId }
import example.readmodel.{ ConcertItem, ConcertRepository }

import scala.concurrent.Future

/** BoxOfficeResource の 共通テスト を定義する
  *
  * テスト共通化のため、このような形式でテストケースを実装する。
  *
  * テスト共通化の方法については、
  * [[https://www.scalatest.org/user_guide/sharing_tests Sharing tests]]
  * を参照すること
  */
trait BoxOfficeResourceBehaviors {
  this: RouteSpecBase =>

  import example.application.http.protocol._
  import example.model.concert.service.BoxOfficeService._

  val idGenerator = new ConcertIdGenerator()

  def boxOfficeResource(
      newResource: (BoxOfficeService, ConcertRepository) => MainHttpApiServerResource,
  ): Unit = {

    "create the concert" in {
      val service    = mock[BoxOfficeService]
      val repository = mock[ConcertRepository]
      val routes     = newResource(service, repository).routes

      val id           = idGenerator.nextId()
      val numOfTickets = 3
      val requestBody =
        s"""
           |{
           |  "tickets": $numOfTickets
           |}
           |""".stripMargin
      val request = Post(s"/concerts/${id.value}")
        .withEntity(HttpEntity(ContentTypes.`application/json`, requestBody))

      service.createConcert(id, numOfTickets) returns
      Future.successful(CreateConcertResponse(numOfTickets))

      request ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        val resp = responseAs[CreateConcertResponseBody]
        resp.id shouldBe id
        resp.tickets shouldBe numOfTickets

        service.createConcert(id, numOfTickets) was called
      }
    }

    "get the concert" in {
      val service    = mock[BoxOfficeService]
      val repository = mock[ConcertRepository]
      val routes     = newResource(service, repository).routes

      val id      = idGenerator.nextId()
      val request = Get(s"/concerts/${id.value}")

      val availableTickets = Vector(ConcertTicketId(0))
      service.getConcert(id) returns
      Future.successful(GetConcertResponse(availableTickets, cancelled = false))

      request ~> routes ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[GetConcertResponseBody]
        resp.id shouldBe id
        resp.tickets shouldBe availableTickets.size
        resp.cancelled shouldBe false

        service.getConcert(id) was called
      }
    }

    "cancel the concert" in {
      val service    = mock[BoxOfficeService]
      val repository = mock[ConcertRepository]
      val routes     = newResource(service, repository).routes

      val id      = idGenerator.nextId()
      val request = Post(s"/concerts/${id.value}/cancel")

      val availableNumOfTickets = 10
      service.cancelConcert(id) returns
      Future.successful(CancelConcertResponse(availableNumOfTickets))

      request ~> routes ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[CancelConcertResponseBody]
        resp.id shouldBe id
        resp.tickets shouldBe availableNumOfTickets

        service.cancelConcert(id) was called
      }
    }

    "buy tickets" in {
      val service    = mock[BoxOfficeService]
      val repository = mock[ConcertRepository]
      val routes     = newResource(service, repository).routes

      val id           = idGenerator.nextId()
      val numOfTickets = 3
      val requestBody =
        s"""
           |{
           |  "tickets": $numOfTickets
           |}
           |""".stripMargin
      val request = Post(s"/concerts/${id.value}/tickets")
        .withEntity(HttpEntity(ContentTypes.`application/json`, requestBody))

      val boughtTickets = (0 until numOfTickets).map(ConcertTicketId).toVector
      assume(boughtTickets.size == numOfTickets)

      service.buyConcertTickets(id, numOfTickets) returns
      Future.successful(BuyConcertTicketsResponse(boughtTickets))

      request ~> routes ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[BuyConcertTicketsResponseBody]
        resp.id shouldBe id
        resp.tickets shouldBe boughtTickets
        resp.tickets.size shouldBe numOfTickets

        service.buyConcertTickets(id, numOfTickets) was called
      }
    }

    "get all concerts" in {
      val service    = mock[BoxOfficeService]
      val repository = mock[ConcertRepository]
      val routes     = newResource(service, repository).routes

      val request = Get("/concerts")

      val id1 = idGenerator.nextId()
      val id2 = idGenerator.nextId()

      repository.fetchConcertList(excludeCancelled = false) returns
      Future.successful(
        Seq(
          ConcertItem(id1, numberOfTickets = 12, cancelled = false),
          ConcertItem(id2, numberOfTickets = 1, cancelled = true),
        ),
      )

      request ~> routes ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[GetConcertsResponseBody]

        resp.items shouldBe Vector(
          GetConcertsResponseBodyItem(id1, tickets = 12, cancelled = false),
          GetConcertsResponseBodyItem(id2, tickets = 1, cancelled = true),
        )

        repository.fetchConcertList(excludeCancelled = false) was called
      }
    }

    "get all available concerts" in {
      val service    = mock[BoxOfficeService]
      val repository = mock[ConcertRepository]
      val routes     = newResource(service, repository).routes

      val request = Get("/concerts?excludeCancelled=true")

      val id1 = idGenerator.nextId()

      repository.fetchConcertList(excludeCancelled = true) returns
      Future.successful(
        Seq(
          ConcertItem(id1, numberOfTickets = 3, cancelled = false),
        ),
      )

      request ~> routes ~> check {
        status shouldBe StatusCodes.OK

        val resp = responseAs[GetConcertsResponseBody]
        resp.items shouldBe Vector(
          GetConcertsResponseBodyItem(id1, tickets = 3, cancelled = false),
        )

        repository.fetchConcertList(excludeCancelled = true) was called
      }
    }

  }

}
