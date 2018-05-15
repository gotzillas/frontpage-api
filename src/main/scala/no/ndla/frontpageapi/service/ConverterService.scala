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
      toApiArticleCollection(sub.latestContent)
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

  def toDomainSubjectPage(subject: api.NewOrUpdateSubjectFrontPageData): domain.SubjectFrontPageData = {
    domain.SubjectFrontPageData(
      None,
      subject.displayInTwoColumns,
      subject.twitter,
      subject.facebook,
      subject.bannerImageId,
      subject.subjectListLocation,
      toDomainAboutSubject(subject.about),
      toDomainSubjectTopical(subject.topical),
      toDomainArticleCollection(subject.mostRead),
      toDomainArticleCollection(subject.editorsChoices),
      toDomainArticleCollection(subject.latestContent)
    )
  }

  private def toDomainSubjectTopical(topical: api.SubjectTopical): domain.SubjectTopical =
    domain.SubjectTopical(topical.location, topical.id)

  private def toDomainArticleCollection(coll: api.ArticleCollection): domain.ArticleCollection =
    domain.ArticleCollection(coll.location, coll.articleIds)

  private def toDomainAboutSubject(about: api.AboutSubject): domain.AboutSubject =
    domain.AboutSubject(about.location, about.title, about.description, about.visualElement)

  private def createImageUrl(id: Long): String = s"${FrontpageApiProperties.RawImageApiUrl}/id/$id"
}
