/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.service

import no.ndla.frontpageapi.model.api
import no.ndla.frontpageapi.model.api.SubjectPageId
import no.ndla.frontpageapi.repository.{FrontPageRepository, SubjectPageRepository}

trait ReadService {
  this: SubjectPageRepository with FrontPageRepository =>
  val readService: ReadService

  class ReadService {

    def getIdFromExternalId(nid: String): Option[SubjectPageId] =
      subjectPageRepository.getIdFromExternalId(nid).map(id => SubjectPageId(id))

    def subjectPage(id: Long): Option[api.SubjectPageData] =
      subjectPageRepository.withId(id).map(ConverterService.toApiSubjectPage)

    def frontPage: Option[api.FrontPageData] = {
      frontPageRepository.get.map(ConverterService.toApiFrontPage)
    }
  }
}
