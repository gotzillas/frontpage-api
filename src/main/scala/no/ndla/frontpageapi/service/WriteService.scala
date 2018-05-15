/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.service

import com.typesafe.scalalogging.LazyLogging
import no.ndla.frontpageapi.model.domain.Errors.NotFoundException
import no.ndla.frontpageapi.repository.{FrontPageRepository, SubjectPageRepository}
import no.ndla.frontpageapi.model.{api, domain}

import scala.util.{Failure, Try}

trait WriteService {
  this: SubjectPageRepository with FrontPageRepository =>
  val writeService: WriteService

  class WriteService extends LazyLogging {

    def newSubjectPage(subject: api.NewOrUpdateSubjectFrontPageData): Try[api.SubjectPageData] = {
      val domainSubjectPage = ConverterService.toDomainSubjectPage(subject)
      subjectPageRepository
        .newSubjectPage(domainSubjectPage)
        .map(ConverterService.toApiSubjectPage)
    }

    def updateSubjectPage(id: Long, subject: api.NewOrUpdateSubjectFrontPageData): Try[api.SubjectPageData] = {
      if (subjectPageRepository.exists(id)) {
        val domainSubjectPage =
          ConverterService.toDomainSubjectPage(id, subject)
        subjectPageRepository
          .updateSubjectPage(domainSubjectPage)
          .map(ConverterService.toApiSubjectPage)
      } else {
        Failure(NotFoundException(id))
      }
    }

    def updateFrontPage(page: api.FrontPageData): Try[api.FrontPageData] = {
      val domainFrontpage = ConverterService.toDomainFrontPage(page)
      val updateFunc =
        if (frontPageRepository.exists) frontPageRepository.updateFrontPage _
        else frontPageRepository.newFrontPage _

      updateFunc(domainFrontpage).map(ConverterService.toApiFrontPage)
    }

  }

}
