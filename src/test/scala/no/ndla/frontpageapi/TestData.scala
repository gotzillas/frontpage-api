/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi

import no.ndla.frontpageapi.model.domain
import io.circe.syntax._
import io.circe.generic.auto._

object TestData {

  val domainSubjectPage = domain.SubjectFrontPageData(
    Some(1),
    "@ndla_samfunn",
    "NDLAsamfunnsfag",
    29668,
    "top",
    domain.AboutSubject("bottom", "Om Samfunnsfag", "Dette er samfunnsfag", """<embed data-resource="image" data-id="123" />"""),
    domain.SubjectTopical("top-left", "urn:resource:1:170252"),
    domain.ArticleCollection(
      "top",
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204")),
    domain.ArticleCollection(
      "top",
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204")),
    domain.ArticleCollection(
      "top",
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204"))
  )
  val domainSubjectJson = domainSubjectPage.asJson.noSpaces
}
