package example.adapter

import example.SpecBase
import example.adapter.ConcertError.IllegalConcertIdError

class ConcertIdSpec extends SpecBase {
  "should be non empty string" in {
    val id = ConcertId.fromString("abc") match {
      case Right(value) => value
      case Left(error)  => fail(error.toString)
    }
    id.value shouldBe "abc"
  }
  "cannot not be empty" in {
    val error = ConcertId.fromString("").left.value
    error shouldBe a[IllegalConcertIdError]
  }
  "cannot not be only whitespace" in {
    val error = ConcertId.fromString("   ").left.value
    error shouldBe a[IllegalConcertIdError]
  }
  "should start with a alphabet" in {
    val id = ConcertId.fromString("a123") match {
      case Right(value) => value
      case Left(error)  => fail(error.toString)
    }
    id.value shouldBe "a123"
  }
  "should not start with a number" in {
    val error = ConcertId.fromString("1ab").left.value
    error shouldBe a[IllegalConcertIdError]
  }
  "should not start with a space" in {
    val error = ConcertId.fromString(" ab").left.value
    error shouldBe a[IllegalConcertIdError]
  }
  "should not contain question mark" in {
    val error = ConcertId.fromString("a?").left.value
    error shouldBe a[IllegalConcertIdError]
  }
}
