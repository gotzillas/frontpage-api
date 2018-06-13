/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.service

import no.ndla.frontpageapi.model.domain.{Errors, VisualElementType}
import no.ndla.frontpageapi.{FrontpageApiProperties, TestData, TestEnvironment, UnitSuite}

import scala.util.Failure

class ConverterServiceTest extends UnitSuite with TestEnvironment {

  test("toApiSubjectPage should convert visual element id to url") {
    val visualElement = TestData.domainSubjectPage.about.visualElement.copy(`type` = VisualElementType.Image)
    val about = TestData.domainSubjectPage.about.copy(visualElement = visualElement)
    val page = TestData.domainSubjectPage.copy(about = about)

    ConverterService.toApiSubjectPage(page).about.visualElement.url should equal(
      s"http://api-gateway.ndla-local/image-api/raw/id/${visualElement.id}")

    val visualElement2 = TestData.domainSubjectPage.about.visualElement.copy(`type` = VisualElementType.Brightcove)
    val about2 = TestData.domainSubjectPage.about.copy(visualElement = visualElement2)
    val page2 = TestData.domainSubjectPage.copy(about = about2)

    val expected =
      s"https://players.brightcove.net/${FrontpageApiProperties.BrightcoveAccountId}/default_default/index.html?videoId=${visualElement2.id}"
    ConverterService.toApiSubjectPage(page2).about.visualElement.url should equal(expected)
  }

  test("toDomainSubjectPage should return a failure if visual element type is invalid") {
    val visualElement = TestData.apiSubjectPage.about.visualElement.copy(`type` = "not an image")
    val about = TestData.apiSubjectPage.about.copy(visualElement = visualElement)
    val page = TestData.apiSubjectPage.copy(about = about)

    val Failure(res: Errors.ValidationException) = ConverterService.toDomainSubjectPage(page)
    res.message should equal("'not an image' is an invalid visual element type")
  }

  test("toDomainSubjectPage should return a success if visual element type is valid") {
    val visualElement = TestData.apiSubjectPage.about.visualElement.copy(`type` = "image")
    val about = TestData.apiSubjectPage.about.copy(visualElement = visualElement)
    val page = TestData.apiSubjectPage.copy(about = about)

    ConverterService.toDomainSubjectPage(page).isSuccess should be(true)
  }
}
