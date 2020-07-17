/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.service

import no.ndla.frontpageapi.model.api.FilmFrontPageData
import no.ndla.frontpageapi.model.domain.{Errors, VisualElementType}
import no.ndla.frontpageapi.{FrontpageApiProperties, TestData, TestEnvironment, UnitSuite}

import scala.util.Failure

class ConverterServiceTest extends UnitSuite with TestEnvironment {

  test("toApiSubjectPage should convert visual element id to url") {
    val visualElement = TestData.domainSubjectPage.about.head.visualElement.copy(`type` = VisualElementType.Image)
    val about = TestData.domainSubjectPage.about.map(_.copy(visualElement = visualElement))
    val page = TestData.domainSubjectPage.copy(about = about)

    ConverterService.toApiSubjectPage(page, "nb").about.get.visualElement.url should equal(
      s"http://api-gateway.ndla-local/image-api/raw/id/${visualElement.id}")

    val visualElement2 = TestData.domainSubjectPage.about.head.visualElement.copy(`type` = VisualElementType.Brightcove)
    val about2 = TestData.domainSubjectPage.about.map(_.copy(visualElement = visualElement2))
    val page2 = TestData.domainSubjectPage.copy(about = about2)

    val expected =
      s"https://players.brightcove.net/${FrontpageApiProperties.BrightcoveAccountId}/${FrontpageApiProperties.BrightcovePlayer}_default/index.html?videoId=${visualElement2.id}"
    ConverterService.toApiSubjectPage(page2, "nb").about.get.visualElement.url should equal(expected)
  }

  test("toDomainSubjectPage should return a failure if visual element type is invalid") {
    val visualElement = TestData.apiSubjectPage.about.head.visualElement.copy(`type` = "not an image")
    val about = TestData.apiSubjectPage.about.map(_.copy(visualElement = visualElement))
    val page = TestData.apiSubjectPage.copy(about = about)

    val Failure(res: Errors.ValidationException) = ConverterService.toDomainSubjectPage(page)
    res.message should equal("'not an image' is an invalid visual element type")
  }

  test("toDomainSubjectPage should return a success if visual element type is valid") {
    val visualElement = TestData.apiSubjectPage.about.head.visualElement.copy(`type` = "image")
    val about = TestData.apiSubjectPage.about.map(_.copy(visualElement = visualElement))
    val page = TestData.apiSubjectPage.copy(about = about)

    ConverterService.toDomainSubjectPage(page).isSuccess should be(true)
  }

  test("toDomainSubjectPage from patch should return a failure if layout is invalid"){
    val page = TestData.apiUpdatedSubjectPage.copy(layout = Some("blob"))

    val Failure(res: Errors.ValidationException) = ConverterService.toDomainSubjectPage(TestData.domainSubjectPage, page)
    res.message should equal("'blob' is an invalid layout")
  }

  test("Should get all languages if nothing is specified") {
    val apiFilmFrontPage = ConverterService.toApiFilmFrontPage(TestData.domainFilmFrontPage, None)
    apiFilmFrontPage.about.length should equal(2)
    apiFilmFrontPage.about.map(_.language) should equal(Seq("nb", "en"))
  }

  test("Should get only specified language") {
    val apiFilmFrontPage = ConverterService.toApiFilmFrontPage(TestData.domainFilmFrontPage, Some("nb"))
    apiFilmFrontPage.about.length should equal(1)
    apiFilmFrontPage.about.map(_.language) should equal(Seq("nb"))

  }
}
