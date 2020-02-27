package no.ndla.frontpageapi

import cats.effect.IO
import cats.implicits._
import no.ndla.frontpageapi.FrontpageApiProperties.{ContactEmail, ContactName}
import no.ndla.frontpageapi.controller.{HealthController, NdlaMiddleware}
import org.http4s.HttpRoutes
import org.http4s.rho.{RhoRoute, RhoRoutes}
import org.http4s.rho.bits.PathAST._
import org.http4s.rho.swagger.models.{Contact, Info, License}
import org.http4s.rho.swagger.syntax.io._

import scala.language.higherKinds
import shapeless.HNil

trait ServiceWithMountPoint {
  val mountPoint: String
  def toRoutes: HttpRoutes[IO]
}

case class Service(service: HttpRoutes[IO], override val mountPoint: String) extends ServiceWithMountPoint {
  override def toRoutes: HttpRoutes[IO] = service
}

case class SwaggerService(service: RhoRoutes[IO], override val mountPoint: String) extends ServiceWithMountPoint {
  override def toRoutes: HttpRoutes[IO] = NdlaMiddleware(service.toRoutes())
}

object Routes {

  def buildRoutes(): List[ServiceWithMountPoint] = {
    val frontPage = SwaggerService(ComponentRegistry.frontPageController, "/frontpage-api/v1/frontpage")
    val subjectPage = SwaggerService(ComponentRegistry.subjectPageController, "/frontpage-api/v1/subjectpage")
    val filmFrontPage = SwaggerService(ComponentRegistry.filmPageController, "/frontpage-api/v1/filmfrontpage")

    List(
      frontPage,
      subjectPage,
      filmFrontPage,
      SwaggerService(ComponentRegistry.internController, "/intern"),
      Service(HealthController(), "/health"),
      Service(createSwaggerDocService(frontPage, subjectPage, filmFrontPage), "/frontpage-api/api-docs")
    )
  }

  private def toTypedPath[F[_]](prefix: String): TypedPath[F, HNil] = {
    val start: PathRule = PathMatch("")
    val newPath =
      prefix.split("/").foldLeft(start)((p, s) => PathAnd(p, PathMatch(s)))

    TypedPath(newPath)
  }

  private def createSwaggerDocService(services: SwaggerService*): HttpRoutes[IO] = {
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
}
