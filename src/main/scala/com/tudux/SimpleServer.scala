package com.tudux

import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.dsl.io._
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.Router
import org.http4s.{HttpRoutes, Request, Response}

//Authentication related
import cats.effect.IO
import cats.implicits._
import dev.profunktor.auth._
import dev.profunktor.auth.jwt._
import pdi.jwt._
import org.http4s._

object SimpleServer extends IOApp {

  case class AuthUser(id: Long, name: String)

  val authenticate: JwtToken => JwtClaim => IO[Option[AuthUser]] =
    {
      //replace with some DB functionality
      token => claim => AuthUser(123L, "joe").some.pure[IO]
      //sample token to use: eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MTIzLCJuYW1lIjoiam9lIn0.xppYLYeQ2VrL3dtBNEaeENgAwIuY4fRqAwz32LN06Ls
    }
  val jwtAuth = JwtAuth.hmac("53cr3t", JwtAlgorithm.HS256)

  val middleware = JwtAuthMiddleware[IO, AuthUser](jwtAuth, authenticate)

  val route: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "length" / str => Ok(str.length.toString)
  }

  //val routes: AuthedRoutes[AuthUser, IO] = ???
  val helloRoutes: AuthedRoutes[AuthUser, IO] =
    AuthedRoutes.of {
      case GET -> Root / "hello" as user => Ok(s"Hello, ${user.name}")
    }

  val securedRoutes: HttpRoutes[IO] = middleware(helloRoutes)
  //val securedRoutes: HttpRoutes[IO] = middleware(route)

//  val app: Kleisli[IO,Request[IO], Response[IO]] = Router(
//    "/" -> route
//  ).orNotFound
    val app: Kleisli[IO,Request[IO], Response[IO]] = Router(
      "/" -> securedRoutes
    ).orNotFound

  override def run(args: List[String]): IO[ExitCode] = {

    BlazeServerBuilder[IO]
      .bindHttp(10001, "localhost")
      .withHttpApp(app)
      .resource
      .useForever
      .as(ExitCode.Success)
  }
}
