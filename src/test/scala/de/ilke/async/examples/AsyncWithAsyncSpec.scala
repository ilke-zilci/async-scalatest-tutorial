package de.ilke.async.examples

import org.scalatest._

import scala.annotation.tailrec
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

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

case class Note(id: Option[Long], content: String)

trait NotesRepository {
  def addOrUpdateNote(note: Note)(implicit ec: ExecutionContext): Future[Note]
  def getNote(id: Long)(implicit ec: ExecutionContext): Future[Option[Note]]
}

class InMemoryNotesRepository(notes: Map[Long, Note] = Map.empty) extends NotesRepository {

  val _notes: mutable.Map[Long, Note] = mutable.Map(notes.toSeq: _*)

  override def addOrUpdateNote(note: Note)(implicit ec: ExecutionContext): Future[Note] = {
    note.id match {
      case None =>
        val id = generateId
        _notes += id -> note
        Future.successful(note.copy(id = Some(id)))
      case Some(id) =>
        _notes.remove(id)
        _notes += id -> note
        Future.successful(note)
    }
  }

  override def getNote(id: Long)(implicit ec: ExecutionContext): Future[Option[Note]] = {
    Future.successful(_notes.get(id))
  }

  @tailrec
  private def generateId: Long = {
    val potentialId = scala.util.Random.nextLong()
    if (!_notes.keySet(potentialId)) potentialId else generateId
  }
}

class NotesSpec extends fixture.AsyncFlatSpec with Matchers {
  type FixtureParam = NotesRepository

  override def withFixture(test: OneArgAsyncTest): FutureOutcome = {
    var repo = new InMemoryNotesRepository
    complete {
      repo.addOrUpdateNote(Note(None, content = "don't forget to"))
      repo.addOrUpdateNote(Note(None, content = "reminder"))
      repo.addOrUpdateNote(Note(None, content = "what was it"))
      withFixture(test.toNoArgAsyncTest(repo))
    } lastly {
      repo = new InMemoryNotesRepository
    }
  }

  "Notes" should "be queried by id" in { repo =>
    val note: Future[Note]                    = repo.addOrUpdateNote(Note(None, "sth"))
    val futureMaybeNote: Future[Option[Note]] = note.flatMap(actualNote => repo.getNote(actualNote.id.getOrElse(0L)))
    val eventualAssertion: Future[Assertion] = futureMaybeNote map { maybeNote =>
      maybeNote shouldBe Some(Note(None, "sth"))
    }
    eventualAssertion

  /*    val foo: Future[Assertion] = for {
      note      <- repo.addOrUpdateNote(Note(None, "sth"))
      id: Long  <- note.id
      maybeNote <- repo.getNote(id)
    } yield maybeNote shouldBe Some(Note(note.id, "sth"))

    foo*/
  }

}
