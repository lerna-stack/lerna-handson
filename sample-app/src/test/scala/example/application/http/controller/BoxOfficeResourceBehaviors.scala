package example.application.http.controller

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, StatusCodes }
import example.RouteSpecBase
import example.application.http.MainHttpApiServerResource
import example.model.concert.{ ConcertIdGenerator, ConcertTicketId }
import example.usecase.{ BoxOfficeReadModelUseCase, BoxOfficeUseCase }

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
  import example.usecase.BoxOfficeReadModelUseCaseProtocol._
  import example.usecase.BoxOfficeUseCaseProtocol._

  val idGenerator = new ConcertIdGenerator()

  def boxOfficeResource(
      newResource: (BoxOfficeUseCase, BoxOfficeReadModelUseCase) => MainHttpApiServerResource,
  ): Unit = {

    "create the concert" in {
      val useCase   = mock[BoxOfficeUseCase]
      val rmUseCase = mock[BoxOfficeReadModelUseCase]
      val routes    = newResource(useCase, rmUseCase).routes

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

      useCase.createConcert(id, numOfTickets) returns
      Future.successful(CreateConcertResponse(numOfTickets))

      request ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        val resp = responseAs[CreateConcertResponseBody]
        resp.id shouldBe id
        resp.tickets shouldBe numOfTickets

        useCase.createConcert(id, numOfTickets) was called
      }
    }

    "get the concert" in {
      val useCase   = mock[BoxOfficeUseCase]
      val rmUseCase = mock[BoxOfficeReadModelUseCase]
      val routes    = newResource(useCase, rmUseCase).routes

      val id      = idGenerator.nextId()
      val request = Get(s"/concerts/${id.value}")

      val availableTickets = Vector(ConcertTicketId(0))
      useCase.getConcert(id) returns
      Future.successful(GetConcertResponse(availableTickets, cancelled = false))

      request ~> routes ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[GetConcertResponseBody]
        resp.id shouldBe id
        resp.tickets shouldBe availableTickets.size
        resp.cancelled shouldBe false

        useCase.getConcert(id) was called
      }
    }

    "cancel the concert" in {
      val useCase   = mock[BoxOfficeUseCase]
      val rmUseCase = mock[BoxOfficeReadModelUseCase]
      val routes    = newResource(useCase, rmUseCase).routes

      val id      = idGenerator.nextId()
      val request = Post(s"/concerts/${id.value}/cancel")

      val availableNumOfTickets = 10
      useCase.cancelConcert(id) returns
      Future.successful(CancelConcertResponse(availableNumOfTickets))

      request ~> routes ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[CancelConcertResponseBody]
        resp.id shouldBe id
        resp.tickets shouldBe availableNumOfTickets

        useCase.cancelConcert(id) was called
      }
    }

    "buy tickets" in {
      val useCase   = mock[BoxOfficeUseCase]
      val rmUseCase = mock[BoxOfficeReadModelUseCase]
      val routes    = newResource(useCase, rmUseCase).routes

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

      useCase.buyConcertTickets(id, numOfTickets) returns
      Future.successful(BuyConcertTicketsResponse(boughtTickets))

      request ~> routes ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[BuyConcertTicketsResponseBody]
        resp.id shouldBe id
        resp.tickets shouldBe boughtTickets
        resp.tickets.size shouldBe numOfTickets

        useCase.buyConcertTickets(id, numOfTickets) was called
      }
    }

    "get all concerts" in {
      val useCase   = mock[BoxOfficeUseCase]
      val rmUseCase = mock[BoxOfficeReadModelUseCase]
      val routes    = newResource(useCase, rmUseCase).routes

      val request = Get("/concerts")

      val id1 = idGenerator.nextId()
      val id2 = idGenerator.nextId()

      rmUseCase.getConcertList(excludeCancelled = false) returns
      Future.successful(
        GetConcertListResponse(
          Seq(
            GetConcertItemResponse(id1, numberOfTickets = 12, cancelled = false),
            GetConcertItemResponse(id2, numberOfTickets = 1, cancelled = true),
          ),
        ),
      )

      request ~> routes ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[GetConcertsResponseBody]

        resp.items shouldBe Vector(
          GetConcertsResponseBodyItem(id1, tickets = 12, cancelled = false),
          GetConcertsResponseBodyItem(id2, tickets = 1, cancelled = true),
        )

        rmUseCase.getConcertList(excludeCancelled = false) was called
      }
    }

    "get all available concerts" in {
      val useCase   = mock[BoxOfficeUseCase]
      val rmUseCase = mock[BoxOfficeReadModelUseCase]
      val routes    = newResource(useCase, rmUseCase).routes

      val request = Get("/concerts?excludeCancelled=true")

      val id1 = idGenerator.nextId()

      rmUseCase.getConcertList(excludeCancelled = true) returns
      Future.successful(
        GetConcertListResponse(
          Seq(
            GetConcertItemResponse(id1, numberOfTickets = 3, cancelled = false),
          ),
        ),
      )

      request ~> routes ~> check {
        status shouldBe StatusCodes.OK

        val resp = responseAs[GetConcertsResponseBody]
        resp.items shouldBe Vector(
          GetConcertsResponseBodyItem(id1, tickets = 3, cancelled = false),
        )

        rmUseCase.getConcertList(excludeCancelled = true) was called
      }
    }

  }

}
