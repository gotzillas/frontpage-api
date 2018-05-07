/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi

import cats.effect.IO
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import no.ndla.frontpageapi.FrontpageApiProperties.{ApplicationPort, ContactEmail, ContactName}
import no.ndla.frontpageapi.controller.{FrontPage, NdlaMiddleware, SubjectPage}
import org.http4s.HttpService
import org.http4s.rho.RhoService
import org.http4s.rho.bits.PathAST._
import org.http4s.rho.swagger.models.{Contact, Info, License}
import org.http4s.rho.swagger.syntax.io._
import org.http4s.rho.swagger.syntax.{io => ioSwagger}
import org.http4s.server.blaze.BlazeBuilder
import shapeless.HNil

import scala.language.higherKinds
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

object Main extends StreamApp[IO] with LazyLogging {
  private[this] case class ServiceWithMountpoint(service: RhoService[IO], mountPoint: String) {
    def toService: HttpService[IO] = service.toService()
  }

  private def toTypedPath[F[_]](prefix: String): TypedPath[F, HNil] = {
    val start: PathRule = PathMatch("")
    val newPath =
      prefix.split("/").foldLeft(start)((p, s) => PathAnd(p, PathMatch(s)))

    TypedPath(newPath)
  }

  private def createSwaggerDocService(services: ServiceWithMountpoint*): HttpService[IO] = {
    val info = Info(
      title = "frontpage-api",
      version = "1.0",
      description = "Service for fetching front page data".some,
      termsOfService = "https://ndla.no".some,
      contact = Contact(ContactName, email = Some(ContactEmail)).some,
      license = License("GPL v3.0", "http://www.gnu.org/licenses/gpl-3.0.en.html").some
    )
    val routes =
      services.map(t => t.service./:(toTypedPath(t.mountPoint)).getRoutes)
    val swagger = createSwagger(apiInfo = info)(routes.flatten)

    createSwaggerRoute(swagger, TypedPath(PathMatch(""))).toService()
  }

  def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    logger.info(
      Source
        .fromInputStream(getClass.getResourceAsStream("/log-license.txt"))
        .mkString)
    val frontPage = ServiceWithMountpoint(new FrontPage[IO](ioSwagger), "/frontpage-api/v1/frontpage")
    val subjectPage = ServiceWithMountpoint(new SubjectPage[IO](ioSwagger), "/frontpage-api/v1/subjectpage")
    val swagger = createSwaggerDocService(frontPage, subjectPage)

    val port = ApplicationPort
    logger.info(s"Starting on port $port")

    BlazeBuilder[IO]
      .mountService(NdlaMiddleware(frontPage.toService), frontPage.mountPoint)
      .mountService(NdlaMiddleware(subjectPage.toService), subjectPage.mountPoint)
      .mountService(swagger, "/frontpage-api/api-docs")
      .bindHttp(port, "0.0.0.0")
      .serve
  }
}
