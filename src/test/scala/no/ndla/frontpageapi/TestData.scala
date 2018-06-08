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
    29668,
    0,
    domain.AboutSubject(1,
                        "Om Samfunnsfag",
                        "Dette er samfunnsfag",
                        domain.VisualElement(VisualElementType.Image, "123", "alt text")),
    domain.SubjectTopical(2, "urn:resource:1:170252"),
    domain.ArticleCollection(
      3,
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204")),
    domain.ArticleCollection(
      4,
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204")),
    domain.ArticleCollection(
      5,
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204")),
    domain.GoToCollection(
      6,
      List("urn:resourcetype:movieAndClip", "urn:resourcetype:lectureAndPresentation")
    )
  )
  val domainSubjectJson = domainSubjectPage.asJson.noSpaces

  val apiSubjectPage = api.NewOrUpdateSubjectFrontPageData(
    "Samfunnsfag",
    "14112",
    false,
    "ndla_samfunn",
    "NDLAsamfunnsfag",
    29668,
    0,
    api.NewOrUpdateAboutSubject(1,
                        "Om Samfunnsfag",
                        "Dette er samfunnsfag",
                        api.NewOrUpdatedVisualElement("image", "123", "alt text")),
    api.SubjectTopical(2, "urn:resource:1:170252"),
    api.ArticleCollection(
      3,
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204")),
    api.ArticleCollection(
      4,
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204")),
    api.ArticleCollection(
      5,
      List("urn:resource:1:161411", "urn:resource:1:182176", "urn:resource:1:183636", "urn:resource:1:170204")),
    api.GoToCollection(
      6,
      List("urn:resourcetype:movieAndClip", "urn:resourcetype:lectureAndPresentation")
    )
  )
}
