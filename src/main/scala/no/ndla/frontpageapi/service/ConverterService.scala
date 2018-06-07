/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.service

import no.ndla.frontpageapi.FrontpageApiProperties
import no.ndla.frontpageapi.model.{api, domain}

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
    api.AboutSubject(about.location, about.title, about.description, about.visualElement)

  def toDomainSubjectPage(id: Long, subject: api.NewOrUpdateSubjectFrontPageData): domain.SubjectFrontPageData =
    toDomainSubjectPage(subject).copy(id = Some(id))

  def toDomainGoToCollection(goTo: api.GoToCollection): domain.GoToCollection =
    domain.GoToCollection(goTo.location, goTo.resourceTypeIds)

  def toDomainSubjectPage(subject: api.NewOrUpdateSubjectFrontPageData): domain.SubjectFrontPageData = {
    domain.SubjectFrontPageData(
      None,
      subject.name,
      subject.displayInTwoColumns,
      subject.twitter,
      subject.facebook,
      subject.bannerImageId,
      subject.subjectListLocation,
      toDomainAboutSubject(subject.about),
      toDomainSubjectTopical(subject.topical),
      toDomainArticleCollection(subject.mostRead),
      toDomainArticleCollection(subject.editorsChoices),
      toDomainArticleCollection(subject.latestContent),
      toDomainGoToCollection(subject.goTo)
    )
  }

  private def toDomainSubjectTopical(topical: api.SubjectTopical): domain.SubjectTopical =
    domain.SubjectTopical(topical.location, topical.id)

  private def toDomainArticleCollection(coll: api.ArticleCollection): domain.ArticleCollection =
    domain.ArticleCollection(coll.location, coll.articleIds)

  private def toDomainAboutSubject(about: api.AboutSubject): domain.AboutSubject =
    domain.AboutSubject(about.location, about.title, about.description, about.visualElement)

  def toDomainFrontPage(page: api.FrontPageData): domain.FrontPageData =
    domain.FrontPageData(page.topical, page.categories.map(toDomainSubjectCollection))

  private def toDomainSubjectCollection(coll: api.SubjectCollection): domain.SubjectCollection =
    domain.SubjectCollection(coll.name, coll.subjects)

  private def createImageUrl(id: Long): String = s"${FrontpageApiProperties.RawImageApiUrl}/id/$id"
}
