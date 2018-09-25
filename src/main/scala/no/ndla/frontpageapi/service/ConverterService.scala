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

import scala.util.{Failure, Success, Try}

object ConverterService {

  def toApiFrontPage(page: domain.FrontPageData): api.FrontPageData =
    api.FrontPageData(page.topical, page.categories.map(toApiSubjectCollection))

  private def toApiSubjectCollection(coll: domain.SubjectCollection): api.SubjectCollection =
    api.SubjectCollection(coll.name, coll.subjects.map(sf => api.SubjectFilters(sf.id, sf.filters)))

  private def toApiBannerImage(banner: domain.BannerImage): api.BannerImage =
    api.BannerImage(createImageUrl(banner.mobileImageId),
                    banner.mobileImageId,
                    createImageUrl(banner.desktopImageId),
                    banner.desktopImageId)

  def toApiSubjectPage(sub: domain.SubjectFrontPageData): api.SubjectPageData = {
    api.SubjectPageData(
      sub.id.get,
      sub.name,
      sub.filters,
      sub.layout,
      sub.twitter,
      sub.facebook,
      toApiBannerImage(sub.bannerImage),
      sub.about.map(toApiAboutSubject),
      sub.topical,
      sub.mostRead,
      sub.editorsChoices,
      sub.latestContent,
      sub.goTo
    )
  }

  private def toApiAboutSubject(about: domain.AboutSubject): api.AboutSubject =
    api.AboutSubject(about.title, about.description, toApiVisualElement(about.visualElement))

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

  private def toDomainBannerImage(banner: api.NewOrUpdateBannerImage): domain.BannerImage =
    domain.BannerImage(banner.mobileImageId, banner.desktopImageId)

  def toDomainSubjectPage(subject: api.NewOrUpdateSubjectFrontPageData): Try[domain.SubjectFrontPageData] = {
    val withoutAboutSubject = domain.SubjectFrontPageData(
      None,
      subject.name,
      subject.filters,
      subject.layout,
      subject.twitter,
      subject.facebook,
      toDomainBannerImage(subject.bannerImage),
      None,
      subject.topical,
      subject.mostRead,
      subject.editorsChoices,
      subject.latestContent,
      subject.goTo
    )

    subject.about.map(toDomainAboutSubject) match {
      case Some(Failure(ex))    => Failure(ex)
      case Some(Success(about)) => Success(withoutAboutSubject.copy(about = Some(about)))
      case None                 => Success(withoutAboutSubject)
    }
  }

  private def toDomainAboutSubject(about: api.NewOrUpdateAboutSubject): Try[domain.AboutSubject] = {
    toDomainVisualElement(about.visualElement)
      .map(domain.AboutSubject(about.title, about.description, _))
  }

  private def toDomainVisualElement(visual: api.NewOrUpdatedVisualElement): Try[domain.VisualElement] =
    VisualElementType.fromString(visual.`type`).map(domain.VisualElement(_, visual.id, visual.alt))

  def toDomainFrontPage(page: api.FrontPageData): domain.FrontPageData =
    domain.FrontPageData(page.topical, page.categories.map(toDomainSubjectCollection))

  private def toDomainSubjectCollection(coll: api.SubjectCollection): domain.SubjectCollection =
    domain.SubjectCollection(coll.name, coll.subjects.map(sf => domain.SubjectFilters(sf.id, sf.filters)))

  private def createImageUrl(id: Long): String = createImageUrl(id.toString)
  private def createImageUrl(id: String): String = s"$RawImageApiUrl/id/$id"
}
