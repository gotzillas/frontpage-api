/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2019 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.controller
import cats.Monad
import io.circe.generic.auto._
import io.circe.syntax._
import cats.effect.{Effect, IO}
import no.ndla.frontpageapi.model.api._
import no.ndla.frontpageapi.service.{ReadService, WriteService}
import org.http4s.rho.RhoRoutes
import org.http4s.rho.swagger.SwaggerSyntax

import scala.language.higherKinds
import scala.util.{Failure, Success}

trait FilmPageController {
  this: ReadService with WriteService =>
  val filmPageController: FilmPageController[IO]

  class FilmPageController[F[+ _]: Effect](swaggerSyntax: SwaggerSyntax[F])(implicit F: Monad[F]) extends RhoRoutes[F] {

    import swaggerSyntax._

    "Get data to display on the film front page" **
      GET +? param[Option[String]]("language") |>> { language: Option[String] =>
      {
        readService.filmFrontPage(language) match {
          case Some(s) => Ok(s.asJson.toString)
          case None    => NotFound(Error.notFound)
        }
      }
    }

    "Update film front page" **
      POST ^ NewOrUpdatedFilmFrontPageData.decoder |>> { filmFrontPage: NewOrUpdatedFilmFrontPageData =>
      {
        writeService.updateFilmFrontPage(filmFrontPage) match {
          case Success(s) => Ok(s.asJson.toString)
          case Failure(_) => InternalServerError(Error.generic)
        }
      }
    }

  }
}
