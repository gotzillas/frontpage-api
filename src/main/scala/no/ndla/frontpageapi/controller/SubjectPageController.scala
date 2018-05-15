/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.controller

import cats.Monad
import cats.effect.{Effect, IO}
import no.ndla.frontpageapi.model.api._
import no.ndla.frontpageapi.model.domain.Errors.NotFoundException
import no.ndla.frontpageapi.service.{ReadService, WriteService}
import org.http4s.rho.RhoService
import org.http4s.rho.swagger.SwaggerSyntax

import scala.language.higherKinds
import scala.util.{Failure, Success}

trait SubjectPageController {
  this: ReadService with WriteService =>
  val subjectPageController: SubjectPageController[IO]

  class SubjectPageController[F[+ _]: Effect](swaggerSyntax: SwaggerSyntax[F])(implicit F: Monad[F])
      extends RhoService[F] {

    import swaggerSyntax._

    "Get data to display on a subject page" **
      GET / pathVar[Long]("subject-id", "The subject id") |>> { id: Long =>
      {
        readService.subjectPage(id) match {
          case Some(s) => Ok(s)
          case None    => NotFound(Error.notFound)
        }
      }
    }

    "Create new subject page" **
      POST ^ NewOrUpdateSubjectFrontPageData.decoder |>> { subjectPage: NewOrUpdateSubjectFrontPageData =>
      {
        writeService.newSubjectPage(subjectPage) match {
          case Success(s)  => Ok(s)
          case Failure(ex) => InternalServerError(Error.generic)
        }
      }
    }

    "Update subject page" **
      PUT / pathVar[Long]("subject-id", "The subject id") ^ NewOrUpdateSubjectFrontPageData.decoder |>> {
      (id: Long, subjectPage: NewOrUpdateSubjectFrontPageData) =>
        {
          writeService.updateSubjectPage(id, subjectPage) match {
            case Success(s)                    => Ok(s)
            case Failure(_: NotFoundException) => NotFound(Error.notFound)
            case Failure(ex) =>
              ex.printStackTrace()
              InternalServerError(Error.generic)
          }
        }
    }

  }

}
