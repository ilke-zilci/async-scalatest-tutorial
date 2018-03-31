package de.ilke.async.examples
import cats.{Id, Monad}
import cats.syntax.all._
import org.scalatest.FunSuite
import org.scalatest.Matchers._
import scala.language.higherKinds

import scala.concurrent.{ExecutionContext, Future}

class ClassWithAsyncCollaborators extends FunSuite {

  case class Foo()
  case class Bar()

  // The "traditional" way:

  trait FooRepository {
    def getFoo(implicit ec: ExecutionContext): Future[Foo]
  }

  trait BarRepository {
    def getBar(implicit ec: ExecutionContext): Future[Bar]
  }

  case class Blah(foo: Foo, bar: Bar)

  trait BlahService {
    def getBlah(implicit ec: ExecutionContext): Future[Blah]
  }

  class BlahServiceImpl(fooRepository: FooRepository, barRepository: BarRepository) extends BlahService {
    def getBlah(implicit ec: ExecutionContext): Future[Blah] =
      for {
        foo <- fooRepository.getFoo
        bar <- barRepository.getBar
      } yield Blah(foo, bar)
  }

  // Notice how there is nothing inherently "futuristic" about the implementation of
  // getBlah. It only uses the fact that Future has flatMap and map methods.

  // Consider this generic alternative

  trait FooRepository2[F[_]] {
    def getFoo: F[Foo]
  }

  trait BarRepository2[F[_]] {
    def getBar: F[Bar]
  }

  trait BlahService2[F[_]] {
    def getBlah: F[Blah]
  }

  class BlahService2Impl[F[_]: Monad](fooRepository: FooRepository2[F], barRepository: BarRepository2[F])
      extends BlahService2[F] {
    def getBlah: F[Blah] =
      for {
        foo <- fooRepository.getFoo
        bar <- barRepository.getBar
      } yield Blah(foo, bar)
  }

  // in test:

  class MockFooRepository extends FooRepository2[Id] {
    def getFoo: Id[Foo] = Foo()
  }

  class MockBarRepository extends BarRepository2[Id] {
    def getBar: Id[Bar] = Bar()
  }

  test("service should work") {
    val blahService = new BlahService2Impl[Id](new MockFooRepository, new MockBarRepository)
    blahService.getBlah shouldBe Blah(Foo(), Bar())
  }
}
