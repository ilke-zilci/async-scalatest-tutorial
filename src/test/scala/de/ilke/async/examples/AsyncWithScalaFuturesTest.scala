package de.ilke.async.examples

import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.time.SpanSugar._

class AsyncWithScalaFuturesTest extends FunSuite with Matchers with ScalaFutures {
  // TODO you can also configure how many retries
  override implicit val patienceConfig: PatienceConfig = PatienceConfig(timeout = Span(2, Seconds))

  // TODO mention timeout env specific config might be helpful like akka. dilated

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
