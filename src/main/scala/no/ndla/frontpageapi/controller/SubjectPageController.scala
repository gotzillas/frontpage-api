/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.controller

import cats.effect.Effect
import no.ndla.frontpageapi.model.{ArticleCollection, SubjectFrontPageData, SubjectTopical}
import org.http4s.rho.RhoService
import org.http4s.rho.swagger.SwaggerSyntax

import scala.language.higherKinds

class SubjectPageController[F[_]: Effect](swaggerSyntax: SwaggerSyntax[F]) extends RhoService[F] {
  import swaggerSyntax._

  "Get data to display on a subject page" **
    GET / pathVar[Int] |>> { id: Int =>
    val mockSubjectPage = SubjectFrontPageData(
      id,
      "NDLAsamfunnsfag",
      "@ndla_samfunn",
      "https://test.api.ndla.no/image-api/raw/id/29668",
      SubjectTopical("top-left", "urn:resource:1:170252"),
      "top",
      ArticleCollection(
        "top",
        List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204")),
      ArticleCollection(
        "top",
        List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204")),
      ArticleCollection(
        "top",
        List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204"))
    )

    Ok(mockSubjectPage)
  }

}
