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

import scala.util.{Failure, Success, Try}

trait WriteService {
  this: SubjectPageRepository with FrontPageRepository =>
  val writeService: WriteService

  class WriteService extends LazyLogging {

    def newSubjectPage(subject: api.NewOrUpdateSubjectFrontPageData): Try[api.SubjectPageData] = {
      val domainSubjectPage = ConverterService.toDomainSubjectPage(subject)
      subjectPageRepository
        .newSubjectPage(domainSubjectPage, subject.externalId)
        .map(ConverterService.toApiSubjectPage)
    }

    def updateSubjectPage(id: Long, subject: api.NewOrUpdateSubjectFrontPageData): Try[api.SubjectPageData] = {
      subjectPageRepository.exists(id) match {
        case Success(exists) if exists =>
          val domainSubjectPage =
            ConverterService.toDomainSubjectPage(id, subject)
          subjectPageRepository
            .updateSubjectPage(domainSubjectPage)
            .map(ConverterService.toApiSubjectPage)
        case Success(_) =>
          Failure(NotFoundException(id))
        case Failure(ex) => Failure(ex)
      }
    }

    def updateFrontPage(page: api.FrontPageData): Try[api.FrontPageData] = {
      val domainFrontpage = ConverterService.toDomainFrontPage(page)
      frontPageRepository
        .newFrontPage(domainFrontpage)
        .map(ConverterService.toApiFrontPage)
    }

  }

}
