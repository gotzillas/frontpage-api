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
    false,
    "@ndla_samfunn",
    "NDLAsamfunnsfag",
    29668,
    0,
    domain.AboutSubject(1,
                        "Om Samfunnsfag",
                        "Dette er samfunnsfag",
                        """<embed data-resource="image" data-id="123" />"""),
    domain.SubjectTopical(2, "urn:resource:1:170252"),
    domain.ArticleCollection(
      3,
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204")),
    domain.ArticleCollection(
      4,
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204")),
    domain.ArticleCollection(
      5,
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204"))
  )
  val domainSubjectJson = domainSubjectPage.asJson.noSpaces
}
