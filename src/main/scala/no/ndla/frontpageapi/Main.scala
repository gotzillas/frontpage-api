/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import org.log4s.getLogger
import no.ndla.frontpageapi.FrontpageApiProperties.{ApplicationPort, ContactEmail, ContactName}
import no.ndla.frontpageapi.controller.{FrontPageController, HealthController, NdlaMiddleware, SubjectPageController}
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.implicits._
import org.http4s.rho.{RhoRoute, RhoRoutes}
import org.http4s.rho.bits.PathAST._
import org.http4s.rho.swagger.models.{Contact, Info, License}
import org.http4s.rho.swagger.syntax.io._
import org.http4s.rho.swagger.syntax.{io => ioSwagger}
import shapeless.HNil

import scala.language.higherKinds
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source
import org.http4s.server.blaze.BlazeServerBuilder

object Main extends IOApp {
  private[this] case class ServiceWithMountpoint(service: HttpRoutes[IO], mountPoint: String)
  private[this] case class SwaggerServiceWithMountpoint(service: RhoRoutes[IO], mountPoint: String) {
    def toRoutes: HttpRoutes[IO] = service.toRoutes()
  }
  val logger = getLogger

  private def toTypedPath[F[_]](prefix: String): TypedPath[F, HNil] = {
    val start: PathRule = PathMatch("")
    val newPath =
      prefix.split("/").foldLeft(start)((p, s) => PathAnd(p, PathMatch(s)))

    TypedPath(newPath)
  }

  private def createSwaggerDocService(services: SwaggerServiceWithMountpoint*): HttpRoutes[IO] = {
    val info = Info(
      title = "frontpage-api",
      version = "1.0",
      description = "Service for fetching front page data".some,
      termsOfService = "https://ndla.no".some,
      contact = Contact(ContactName, email = Some(ContactEmail)).some,
      license = License("GPL v3.0", "http://www.gnu.org/licenses/gpl-3.0.en.html").some
    )
    val routes = services.map(t => {
      t.service./:(toTypedPath(t.mountPoint)).getRoutes
    })

    val swagRoutes: scala.collection.immutable.Seq[RhoRoute[IO, _]] = routes.flatten.to[scala.collection.immutable.Seq]
    val swagger = createSwagger(apiInfo = info)(swagRoutes)

    createSwaggerRoute(swagger, TypedPath(PathMatch(""))).toRoutes()
  }

  override def run(args: List[String]): IO[ExitCode] = {
    logger.info(
      Source
        .fromInputStream(getClass.getResourceAsStream("/log-license.txt"))
        .mkString)
    logger.info("Starting database migration")
    DBMigrator.migrate(ComponentRegistry.dataSource)

    logger.info("Building swagger service")
    val subjectPage =
      SwaggerServiceWithMountpoint(ComponentRegistry.subjectPageController, "/frontpage-api/v1/subjectpage")
    val frontPage = SwaggerServiceWithMountpoint(ComponentRegistry.frontPageController, "/frontpage-api/v1/frontpage")
    val filmfrontPage =
      SwaggerServiceWithMountpoint(ComponentRegistry.filmPageController, "/frontpage-api/v1/filmfrontpage")
    val internController = SwaggerServiceWithMountpoint(ComponentRegistry.internController, "/intern")
    val healthController = ServiceWithMountpoint(HealthController(), "/health")
    val swagger =
      ServiceWithMountpoint(createSwaggerDocService(frontPage, subjectPage, filmfrontPage), "/frontpage-api/api-docs")

    logger.info(s"Starting on port $ApplicationPort")

    // TODO: Mountpoints needs to be specified somewhere right?
    val httpApp =
      NdlaMiddleware(frontPage.toRoutes) <+>
        NdlaMiddleware(subjectPage.toRoutes) <+>
        NdlaMiddleware(filmfrontPage.toRoutes) <+>
        NdlaMiddleware(internController.toRoutes) <+>
        healthController.service <+>
        swagger.service

    val x = httpApp.orNotFound

    BlazeServerBuilder[IO]
      .withHttpApp(x)
      .bindHttp(ApplicationPort, "0.0.0.0")
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
