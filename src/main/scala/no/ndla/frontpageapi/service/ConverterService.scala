/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.service

import no.ndla.frontpageapi.FrontpageApiProperties.{BrightcoveAccountId, RawImageApiUrl}
import no.ndla.frontpageapi.model.domain.VisualElementType
import no.ndla.frontpageapi.model.{api, domain}

import scala.util.{Success, Try}

object ConverterService {

  def toApiFrontPage(page: domain.FrontPageData): api.FrontPageData =
    api.FrontPageData(page.topical, page.categories.map(toApiSubjectCollection))

  private def toApiSubjectCollection(coll: domain.SubjectCollection): api.SubjectCollection =
    api.SubjectCollection(coll.name, coll.subjects)

  def toApiGoToCollection(goTo: domain.GoToCollection): api.GoToCollection =
    api.GoToCollection(goTo.location, goTo.resourceTypeIds)

  def toApiSubjectPage(sub: domain.SubjectFrontPageData): api.SubjectPageData = {
    api.SubjectPageData(
      sub.id.get,
      sub.displayInTwoColumns,
      sub.twitter,
      sub.facebook,
      createImageUrl(sub.bannerImageId),
      sub.subjectListLocation,
      toApiAboutSubject(sub.about),
      toApiSubjectTopical(sub.topical),
      toApiArticleCollection(sub.mostRead),
      toApiArticleCollection(sub.editorsChoices),
      toApiArticleCollection(sub.latestContent),
      toApiGoToCollection(sub.goTo)
    )
  }

  private def toApiSubjectTopical(topical: domain.SubjectTopical): api.SubjectTopical =
    api.SubjectTopical(topical.location, topical.id)

  private def toApiArticleCollection(coll: domain.ArticleCollection): api.ArticleCollection =
    api.ArticleCollection(coll.location, coll.articleIds)

  private def toApiAboutSubject(about: domain.AboutSubject): api.AboutSubject =
    api.AboutSubject(about.location, about.title, about.description, toApiVisualElement(about.visualElement))

  private def toApiVisualElement(visual: domain.VisualElement): api.VisualElement = {
    val url = visual.`type` match {
      case VisualElementType.Image => createImageUrl(visual.id.toLong)
      case VisualElementType.Brightcove =>
        s"https://players.brightcove.net/$BrightcoveAccountId/default_default/index.html?videoId=${visual.id}"
    }
    api.VisualElement(visual.`type`.toString, url, visual.alt)
  }

  def toDomainSubjectPage(id: Long, subject: api.NewOrUpdateSubjectFrontPageData): Try[domain.SubjectFrontPageData] =
    toDomainSubjectPage(subject).map(_.copy(id = Some(id)))

  private def toDomainGoToCollection(goTo: api.GoToCollection): domain.GoToCollection =
    domain.GoToCollection(goTo.location, goTo.resourceTypeIds)

  def toDomainSubjectPage(subject: api.NewOrUpdateSubjectFrontPageData): Try[domain.SubjectFrontPageData] = {
    toDomainAboutSubject(subject.about).map(
      aboutSubject =>
        domain.SubjectFrontPageData(
          None,
          subject.name,
          subject.displayInTwoColumns,
          subject.twitter,
          subject.facebook,
          subject.bannerImageId,
          subject.subjectListLocation,
          aboutSubject,
          toDomainSubjectTopical(subject.topical),
          toDomainArticleCollection(subject.mostRead),
          toDomainArticleCollection(subject.editorsChoices),
          toDomainArticleCollection(subject.latestContent),
          toDomainGoToCollection(subject.goTo)
      ))
  }

  private def toDomainSubjectTopical(topical: api.SubjectTopical): domain.SubjectTopical =
    domain.SubjectTopical(topical.location, topical.id)

  private def toDomainArticleCollection(coll: api.ArticleCollection): domain.ArticleCollection =
    domain.ArticleCollection(coll.location, coll.articleIds)

  private def toDomainAboutSubject(about: api.NewOrUpdateAboutSubject): Try[domain.AboutSubject] =
    toDomainVisualElement(about.visualElement)
      .map(domain.AboutSubject(about.location, about.title, about.description, _))

  private def toDomainVisualElement(visual: api.NewOrUpdatedVisualElement): Try[domain.VisualElement] =
    VisualElementType.fromString(visual.`type`).map(domain.VisualElement(_, visual.id, visual.alt))

  def toDomainFrontPage(page: api.FrontPageData): domain.FrontPageData =
    domain.FrontPageData(page.topical, page.categories.map(toDomainSubjectCollection))

  private def toDomainSubjectCollection(coll: api.SubjectCollection): domain.SubjectCollection =
    domain.SubjectCollection(coll.name, coll.subjects)

  private def createImageUrl(id: Long): String = createImageUrl(id.toString)
  private def createImageUrl(id: String): String = s"$RawImageApiUrl/id/$id"
}
