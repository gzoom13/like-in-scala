package ru.tinkoff.likeinscala

import cats.effect.{Async, IO}
import io.getquill.{PostgresJAsyncContext, SnakeCase}

import scala.concurrent.Future

class UserRepository(private val ctx: PostgresJAsyncContext[SnakeCase]) {
  import io.getquill.*
  import ctx.*

  import scala.concurrent.ExecutionContext.Implicits.global

  private val userSchema = quote { querySchema[User]("users") }

  def fromFuture[F[_]: Async, A](a: => Future[A]): F[A] = Async[F].fromFuture(Async[F].delay(a))

  def count: IO[Long] = fromFuture(run(quote { userSchema.map(_.login).size }))

  def findAll: IO[Seq[User]] = fromFuture(run(quote { userSchema }))

}
