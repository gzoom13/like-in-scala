package ru.tinkoff.likeinscala

import cats.Semigroup
import cats.effect.*
import cats.effect.std.{Dispatcher, Env}
import cats.syntax.all.given
import io.getquill.{PostgresJAsyncContext, SnakeCase}
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import sttp.tapir.server.vertx.cats.VertxCatsServerInterpreter
import sttp.tapir.server.vertx.cats.VertxCatsServerInterpreter.*

object Main extends IOApp:

  override def run(args: List[String]): IO[ExitCode] =

    val vertxResources = for {
      vertx <- Resource.make(IO(Vertx.vertx()))(_.close().asF[IO].void)
      server <- Resource.make(IO(vertx.createHttpServer()))(_.close().asF[IO].void)
      router <- Resource.eval(IO(Router.router(vertx)))
      dispatcher <- Dispatcher.sequential[IO]
    } yield (dispatcher, server, router)

    val dbResources = Resource.make(IO(new PostgresJAsyncContext[SnakeCase](SnakeCase, "ctx")))(c => IO.blocking(c.close))

    val application = (vertxResources, dbResources)
      .tupled
      .evalMap { case ((dispatcher, server, router), ctx) =>
      for {
        port <- Env[IO].get("HTTP_PORT").map(_.flatMap(_.toIntOption).getOrElse(8081))
        endpoints = new Endpoints(new UserRepository(ctx))
        bind <- IO
          .delay {
            endpoints.all
              .foreach(endpoint => {
                VertxCatsServerInterpreter[IO](dispatcher)
                  .route(endpoint)
                  .apply(router)
              })
            server.requestHandler(router).listen(port)
          }
          .flatMap(_.asF[IO])
        _ <- IO.println(s"Server started at http://localhost:${bind.actualPort()}.")
      } yield ()
    }

    application.useForever
