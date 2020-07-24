/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.controller

import cats.Monad
import io.circe.generic.auto._
import io.circe.syntax._
import cats.effect.{Effect, IO}
import org.log4s.getLogger
import no.ndla.frontpageapi.model.api._
import no.ndla.frontpageapi.model.domain.Errors.ValidationException
import no.ndla.frontpageapi.service.{ReadService, WriteService}
import org.http4s.rho.RhoRoutes
import org.http4s.rho.swagger.SwaggerSyntax

import scala.language.higherKinds
import scala.util.{Failure, Success}

trait SubjectPageController {
  this: ReadService with WriteService =>
  val subjectPageController: SubjectPageController[IO]

  class SubjectPageController[F[+ _]: Effect](swaggerSyntax: SwaggerSyntax[F])(implicit F: Monad[F])
      extends RhoRoutes[F] {

    import swaggerSyntax._

    "Get data to display on a subject page" **
      GET / pathVar[Long]("subject-id", "The subject id") +? param[String]("language", "nb") & param[Boolean]("fallback", false) |>> {
      (id: Long, language: String, fallback: Boolean) =>
        {
          readService.subjectPage(id, language, fallback) match {
            case Some(s) => Ok(s)
            case None    => NotFound(Error.notFound)
          }
        }
    }

    "Create new subject page" **
      POST ^ NewSubjectFrontPageData.decoder |>> {
      subjectPage: NewSubjectFrontPageData =>
      {
        writeService.newSubjectPage(subjectPage) match {
          case Success(s)                       => Ok(s)
          case Failure(ex: ValidationException) => BadRequest(Error.badRequest(ex.getMessage))
          case Failure(_)                       => InternalServerError(Error.generic)
        }
      }
    }

    "Update subject page" **
      PATCH / pathVar[Long]("subject-id", "The subject id") ^ UpdatedSubjectFrontPageData.decoder |>> {
      (id: Long, subjectPage: UpdatedSubjectFrontPageData) =>
    {
      writeService.updateSubjectPage(id, subjectPage) match {
        case Success(s) => Ok(s.asJson.toString)
        case Failure(_) => InternalServerError(Error.generic)
        }
      }
    }
  }
}
