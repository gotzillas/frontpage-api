/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.controller

import cats.Monad
import cats.effect.{Effect, IO}
import org.log4s.getLogger
import no.ndla.frontpageapi.model.api._
import no.ndla.frontpageapi.service.{ReadService, WriteService}
import org.http4s.rho.RhoService
import org.http4s.rho.swagger.SwaggerSyntax

import scala.language.higherKinds

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

  }
}
