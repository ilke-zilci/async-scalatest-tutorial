package de.ilke.async.examples

import org.scalatest._

import scala.concurrent.Future

class AsyncWithAsyncSpec extends AsyncFunSuite with Matchers {

  //By default, tests in asynchronous style
  // traits will be executed one after another, i.e., serially.

  def doReturnValueNow(): String = "stuff"

  test("just write a sync test in the same test suite") {
    // You can also write synchronous tests. The body
    // must have result type Assertion

    doReturnValueNow() shouldBe "stuff"
  }

  test("map over the result in async test, positive case") {
    val futureResult: Future[String] = new SystemUnderTest().doAsyncOperationAndReturnValue("sunny day")
    // You can map assertions onto a Future, then return
    // the resulting Future[Assertion] to ScalaTest:
    futureResult map { result =>
      result shouldBe "stuff"
    }
  }

  test("should call async func and wait for it to finish with await result, negative case, more readable") {
    val futureEx: Future[RainyDayException] =
      recoverToExceptionIf[RainyDayException] {
        new SystemUnderTest().doAsyncOperationAndReturnValue("rainy day")
      }
    futureEx map { ex =>
      assert(ex.getMessage == "are you joking?")
    }
  }
}
