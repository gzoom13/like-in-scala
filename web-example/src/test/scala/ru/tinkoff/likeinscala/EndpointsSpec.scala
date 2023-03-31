package ru.tinkoff.likeinscala

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import io.circe.generic.auto.*
import io.getquill.{PostgresJAsyncContext, SnakeCase}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3.circe.*
import sttp.client3.testing.SttpBackendStub
import sttp.client3.{UriContext, basicRequest}
import sttp.tapir.integ.cats.CatsMonadError
import sttp.tapir.server.stub.TapirStubInterpreter
import ru.tinkoff.likeinscala.Library.*

class EndpointsSpec extends AnyFlatSpec with Matchers with EitherValues:

  private val ctx: PostgresJAsyncContext[SnakeCase] = new PostgresJAsyncContext[SnakeCase](SnakeCase, "ctx")
  private val e = new Endpoints(new UserRepository(ctx))
  import e.*

  it should "return users list" in {
    // given
    val backendStub = TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
      .whenServerEndpoint(listUsers)
      .thenRunLogic()
      .backend()

    // when
    val response = basicRequest
      .get(uri"http://test.com/")
      .response(asJson[List[User]])
      .send(backendStub)

    // then
    response
      .map(
        _.body.value shouldBe List(
          User("smaldini", "Stéphane", "Maldini"),
          User("sdeleuze", "Sébastien", "Deleuze"),
          User("bclozel", "Brian", "Clozel")
        )
      )
      .unwrap
  }

  it should "list available books" in {
    // given
    val backendStub = TapirStubInterpreter(SttpBackendStub(new CatsMonadError[IO]()))
      .whenServerEndpoint(booksListingServerEndpoint)
      .thenRunLogic()
      .backend()

    // when
    val response = basicRequest
      .get(uri"http://test.com/books/list/all")
      .response(asJson[List[Book]])
      .send(backendStub)

    // then
    response.map(_.body.value shouldBe books).unwrap
  }

  extension [T](t: IO[T]) def unwrap: T = t.unsafeRunSync()
