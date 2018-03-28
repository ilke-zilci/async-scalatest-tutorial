package de.ilke.async.examples

import scala.concurrent.Future

case class RainyDayException(msg: String) extends Exception(msg)
case class SnowyDayException(msg: String) extends Exception(msg)

class SystemUnderTest {

  import scala.concurrent.ExecutionContext.Implicits.global

  def doHiddenAsyncOperation(config: String): Unit = {
    Future {
      Thread.sleep(10)
      if (config == "rainy day") throw RainyDayException("are you joking?")
    }
  }

  def doAsyncOperation(config: String): Future[Unit] = {
    Future {
      Thread.sleep(10)
      if (config == "rainy day") throw RainyDayException("are you joking?")
    }
  }

  def doHiddenAsyncOperationAndReturnValue(config: String): String = {
    Future {
      Thread.sleep(10)
      if (config == "rainy day") throw RainyDayException("are you joking?")
    }
    "stuff"
  }

  def doAsyncOperationAndReturnValue(config: String): Future[String] = {
    Future {
      Thread.sleep(10)
      if (config == "rainy day") throw RainyDayException("are you joking?")
      "stuff"
    }
  }

  def doAsyncOperationSlowly(config: String): Future[String] = {
    Future {
      Thread.sleep(10000)
      if (config == "rainy day") throw RainyDayException("are you joking?")
      "stuff"
    }
  }
}
