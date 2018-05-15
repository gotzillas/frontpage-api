/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.service

import no.ndla.frontpageapi.model.{api, domain}

object ConverterService {

  def toApiSubjectPage(sub: domain.SubjectFrontPageData): api.SubjectPageData = {
    api.SubjectPageData(
      sub.id.get,
      sub.twitter,
      sub.facebook,
      sub.banner,
      toApiSubjectTopical(sub.topical),
      sub.subjectListLocation,
      toApiArticleCollection(sub.mostRead),
      toApiArticleCollection(sub.editorsChoices),
      toApiArticleCollection(sub.latestContent)
    )
  }

  private def toApiSubjectTopical(topical: domain.SubjectTopical): api.SubjectTopical =
    api.SubjectTopical(topical.location, topical.id)

  private def toApiArticleCollection(coll: domain.ArticleCollection): api.ArticleCollection =
    api.ArticleCollection(coll.location, coll.articleIds)

  def toDomainSubjectPage(id: Long, subject: api.NewOrUpdateSubjectFrontPageData): domain.SubjectFrontPageData =
    toDomainSubjectPage(subject).copy(id = Some(id))

  def toDomainSubjectPage(subject: api.NewOrUpdateSubjectFrontPageData): domain.SubjectFrontPageData = {
    domain.SubjectFrontPageData(
      None,
      subject.twitter,
      subject.facebook,
      subject.banner,
      toDomainSubjectTopical(subject.topical),
      subject.subjectListLocation,
      toDomainArticleCollection(subject.mostRead),
      toDomainArticleCollection(subject.editorsChoices),
      toDomainArticleCollection(subject.latestContent)
    )
  }

  private def toDomainSubjectTopical(topical: api.SubjectTopical): domain.SubjectTopical =
    domain.SubjectTopical(topical.location, topical.id)

  private def toDomainArticleCollection(coll: api.ArticleCollection): domain.ArticleCollection =
    domain.ArticleCollection(coll.location, coll.articleIds)
}
