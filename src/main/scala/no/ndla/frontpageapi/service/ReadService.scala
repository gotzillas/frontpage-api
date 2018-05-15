/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.service

import no.ndla.frontpageapi.model.api
import no.ndla.frontpageapi.repository.SubjectPageRepository

trait ReadService {
  this: SubjectPageRepository =>
  val readService: ReadService

  class ReadService {

    def subjectPage(id: Long): Option[api.SubjectPageData] = {
      subjectPageRepository.withId(id).map(ConverterService.toApiSubjectPage)
    }

  }
}
