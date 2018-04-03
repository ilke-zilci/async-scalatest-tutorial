package de.ilke.async.examples

import org.scalatest.FunSuite

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.scalatest.Matchers._
import scala.concurrent.duration._

class AsyncWithPlainScalaTest extends FunSuite {

  test("should call hidden async func, assert on the result, incorrectly passes") {
    val result = new SystemUnderTest().doHiddenAsyncOperationAndReturnValue("rainy day")
    result shouldBe "stuff"
  }

  // Your function with side effects return unit
  test("should call hidden async func which returns unit, incorrectly passes, no proper assertion anyway") {
    new SystemUnderTest().doHiddenAsyncOperation("rainy day")
  }

  test("should call async func which returns unit and wait for it to finish with await result, negative case") {
    try {
      val result: Unit = Await.result(
        new SystemUnderTest().doAsyncOperation("rainy day"),
        Duration.Inf
      )
    } catch {
      case RainyDayException(e) => e shouldBe "are you joking?"
      case _: Throwable         => fail
    }
  }

  test("should call async func which returns unit and wait for it to finish with await result, positive case") {
    val result: Unit = Await.result(
      new SystemUnderTest().doAsyncOperation("sunny day"),
      Duration.Inf
    )
    // assert what?
  }

  test("should call async func and wait for it to finish with await result, negative case, more readable") {
    val result =
      intercept[RainyDayException] {
        Await.result(
          new SystemUnderTest().doAsyncOperationAndReturnValue("rainy day"),
          Duration.Inf
        )
      }
    assert(result.getMessage === "are you joking?")
  }

  test("should call async func and wait for it to finish with await result, positive case, time out") {
    val result =
      Await.result(
        new SystemUnderTest().doAsyncOperationAndReturnValue("sunny day"),
        50 milliseconds
      )

    result shouldBe "stuff"
  }
}
