package ru.tinkoff.likeinscala

import cats.effect.kernel.Clock
import cats.effect.std.{Console, Env}
import cats.effect.{IO, IOApp, Ref, Temporal}
import cats.syntax.all.*
import cats.{Functor, Monad}
import monocle.syntax.all.focus
import sttp.client3.http4s.Http4sBackend
import sttp.client3.{SttpBackend, UriContext, emptyRequest}

import scala.concurrent.duration.DurationInt

object State extends IOApp.Simple {

  case class State(counter: Int)

  override val run: IO[Unit] = for {
    initialCounter <- initCounter[IO].rethrow
    state          <- Ref.of[IO, State](State(initialCounter))
    _              <- state.update(_.focus(_.counter).modify(_ + 1))
    newState       <- state.get
    _              <- IO.println("State after update is " + newState)
    b              <- Http4sBackend.usingDefaultBlazeClientBuilder[IO]().allocated
    given SttpBackend[IO, _] = b._1
    fetchedCounter <- fetchCounter3[IO].rethrow
    _              <- state.update(_.focus(_.counter).set(fetchedCounter))
    newerState     <- state.get
    _              <- IO.println("State after fetch is " + newerState)
  } yield ()

  private def initCounter[F[_]: Functor: Env]: F[Either[IllegalArgumentException, Int]] = {
    val counterEnvVarName = "COUNTER"

    Env[F]
      .get(counterEnvVarName)
      .map(counterToInt)
      .map(
        _.left.map(err =>
          new IllegalArgumentException(
            s"Error in environment variable $counterEnvVarName: ${err.getMessage}"
          )
        )
      )
  }

  private def fetchCounter1[F[_]: Functor: Clock]: F[Either[IllegalArgumentException, Int]] = {
    Clock[F].realTime.map(_.toMillis.toInt.asRight)
  }

  private def fetchCounter2[F[_]: Functor: Console]: F[Either[IllegalArgumentException, Int]] = {
    Console[F].readLine.map(s => counterToInt(s.some))
  }

  private def fetchCounter3[F[_]: Functor](using
      http: SttpBackend[F, _]
  ): F[Either[RuntimeException, Int]] = {
    http
      .send(
        emptyRequest
          .get(
            uri"https://www.random.org/integers/?num=1&min=1&max=3000&col=1&base=10&format=plain&rnd=new"
          )
      )
      .map(_.body)
      .map(_.left.map(new RuntimeException(_)))
      .map(_.flatMap(s => counterToInt(s.trim.some)))
  }

  def counterToInt(maybeCounterString: Option[String]): Either[IllegalArgumentException, Int] = {
    maybeCounterString
      .toRight(s"Expected counter to be set as integer")
      .flatMap(s => s.toIntOption.toRight(s"Expected integer counter, got $s"))
      .left
      .map(new IllegalArgumentException(_))
  }

}
