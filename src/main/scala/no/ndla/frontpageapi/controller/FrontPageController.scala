/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.controller

import cats.effect.Effect
import no.ndla.frontpageapi.model.api.{FrontPageData, SubjectCollection}
import org.http4s.rho.RhoService
import org.http4s.rho.swagger.SwaggerSyntax

import scala.language.higherKinds

class FrontPageController[F[_]: Effect](swaggerSyntax: SwaggerSyntax[F]) extends RhoService[F] {
  import swaggerSyntax._

  "Get data to display on the front page" **
    GET |>> {
    val frontPage = FrontPageData(
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204"),
      List(SubjectCollection("fellesfag", List("urn:subject:1", "urn:subject:2")),
           SubjectCollection("yrkesfag", List("urn:subject:3", "urn:subject:4")))
    )
    Ok(frontPage)
  }

}
