package de.ilke.async.examples

import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.time.SpanSugar._

object AppConfiguration {
  val TEST_TIME_FACTOR_ON_CI    = 3
  val TEST_TIME_FACTOR_ON_LOCAL = 1
}

class AsyncWithScalaFuturesTest extends FunSuite with Matchers with ScalaFutures {
  // timeout : the maximum amount of time to allow unsuccessful queries before giving up and throwing TestFailedException
  // interval: the amount of time to sleep between each query
  import AppConfiguration._

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(2 * TEST_TIME_FACTOR_ON_LOCAL, Seconds), interval = Span(500, Millis))

  test("should call async func and wait for it to finish, time out") {
    val result: String =
      new SystemUnderTest().doAsyncOperationSlowly("sunny day").futureValue
    result shouldBe "stuff"
  }

  test("should call async func and wait for it to finish with whenReady custom timeout") {
    val reqTimeout = Timeout(1.seconds)
    whenReady(new SystemUnderTest().doAsyncOperationSlowly("sunny day"), reqTimeout) { result =>
      result shouldBe "stuff"
    }
  }

  test("should call async func and wait for it to finish with ScalaFutures futureValue negative") {
    whenReady(new SystemUnderTest().doAsyncOperationAndReturnValue("rainy day").failed) {
      case RainyDayException(e) => e shouldBe "are you joking?"
      case _: Throwable         => fail("expected exception not there")
    }
  }

}
