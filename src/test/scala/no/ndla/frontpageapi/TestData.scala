/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi

import no.ndla.frontpageapi.model.{domain, api}
import io.circe.syntax._
import io.circe.generic.auto._
import no.ndla.frontpageapi.model.domain.SubjectFrontPageData._
import no.ndla.frontpageapi.model.domain.VisualElementType

object TestData {

  val domainSubjectPage = domain.SubjectFrontPageData(
    Some(1),
    "Samfunnsfag",
    false,
    "@ndla_samfunn",
    "NDLAsamfunnsfag",
    domain.BannerImage(29668, 29668),
    domain.AboutSubject("Om Samfunnsfag",
                        "Dette er samfunnsfag",
                        domain.VisualElement(VisualElementType.Image, "123", "alt text")),
      "urn:resource:1:170252",
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204"),
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204"),
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204"),
      List("urn:resourcetype:movieAndClip", "urn:resourcetype:lectureAndPresentation")
  )
  val domainSubjectJson = domainSubjectPage.asJson.noSpaces

  val apiSubjectPage = api.NewOrUpdateSubjectFrontPageData(
    "Samfunnsfag",
    "14112",
    false,
    "ndla_samfunn",
    "NDLAsamfunnsfag",
    api.NewOrUpdateBannerImage(29668, 29668),
    api.NewOrUpdateAboutSubject(1,
                                "Om Samfunnsfag",
                                "Dette er samfunnsfag",
                                api.NewOrUpdatedVisualElement("image", "123", "alt text")),
    "urn:resource:1:170252",
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204"),
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204"),
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204"),
      List("urn:resourcetype:movieAndClip", "urn:resourcetype:lectureAndPresentation")
  )
}
