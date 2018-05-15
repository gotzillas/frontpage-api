/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.service

import no.ndla.frontpageapi.model.domain.Errors.NotFoundException
import no.ndla.frontpageapi.repository.SubjectPageRepository
import no.ndla.frontpageapi.model.{api, domain}

import scala.util.{Failure, Try}

trait WriteService {
  this: SubjectPageRepository =>
  val writeService: WriteService

  class WriteService {

    def newSubjectPage(subject: api.NewOrUpdateSubjectFrontPageData): Try[api.SubjectPageData] = {
      val domainSubjectPage = ConverterService.toDomainSubjectPage(subject)
      subjectPageRepository
        .newSubjectPage(domainSubjectPage)
        .map(ConverterService.toApiSubjectPage)
    }

    def updateSubjectPage(id: Long, subject: api.NewOrUpdateSubjectFrontPageData): Try[api.SubjectPageData] = {
      if (subjectPageRepository.exists(id)) {
        val domainSubjectPage = ConverterService.toDomainSubjectPage(id, subject)
        subjectPageRepository
          .updateSubjectPage(domainSubjectPage)
          .map(ConverterService.toApiSubjectPage)
      } else {
        Failure(NotFoundException(id))
      }
    }

  }

}
