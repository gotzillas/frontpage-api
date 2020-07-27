/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.service

import no.ndla.frontpageapi.model.domain.Errors.{NotFoundException, OperationNotAllowedException}
import no.ndla.frontpageapi.repository.{FilmFrontPageRepository, FrontPageRepository, SubjectPageRepository}
import no.ndla.frontpageapi.model.{api, domain}

import scala.util.{Failure, Success, Try}

trait WriteService {
  this: SubjectPageRepository with FrontPageRepository with FilmFrontPageRepository =>
  val writeService: WriteService

  class WriteService {

    def newSubjectPage(subject: api.NewSubjectFrontPageData): Try[api.SubjectPageData] = {
      for {
        convertedSubject <- ConverterService.toDomainSubjectPage(subject)
        subjectPage <- subjectPageRepository.newSubjectPage(convertedSubject, subject.externalId)
      } yield ConverterService.toApiSubjectPage(subjectPage, "nb")
    }

    def updateSubjectPage(id: Long, subject: api.NewSubjectFrontPageData): Try[api.SubjectPageData] = {
      subjectPageRepository.exists(id) match {
        case Success(exists) if exists =>
          for {
            domainSubject <- ConverterService.toDomainSubjectPage(id, subject)
            subjectPage <- subjectPageRepository.updateSubjectPage(domainSubject)
          } yield ConverterService.toApiSubjectPage(subjectPage, "nb")
        case Success(_) =>
          Failure(NotFoundException(id))
        case Failure(ex) => Failure(ex)
      }
    }

    def updateSubjectPage(id: Long, subject: api.UpdatedSubjectFrontPageData): Try[api.SubjectPageData] = {
      subjectPageRepository.withId(id) match {
        case Some(existingSubject) =>
          for {
            domainSubject <- ConverterService.toDomainSubjectPage(existingSubject, subject)
            subjectPage <- subjectPageRepository.updateSubjectPage(domainSubject)
          } yield ConverterService.toApiSubjectPage(subjectPage, subject.about.get.language)
        /* case None if subjectPageRepository.exists(id) =>
          for {
            domainSubject <- ConverterService.toDomainSubjectPage(id, subject) //trenger NewSubjectFrontPageData i denne metoden
            subjectPage <- subjectPageRepository.updateSubjectPage(domainSubject)
          } yield ConverterService.toApiSubjectPage(subjectPage, "nb")*/
        case None =>
          Failure(NotFoundException(404))
      }
    }

    def updateFrontPage(page: api.FrontPageData): Try[api.FrontPageData] = {
      val domainFrontpage = ConverterService.toDomainFrontPage(page)
      frontPageRepository
        .newFrontPage(domainFrontpage)
        .map(ConverterService.toApiFrontPage)
    }

    def updateFilmFrontPage(page: api.NewOrUpdatedFilmFrontPageData): Try[api.FilmFrontPageData] = {
      val domainFilmFrontPageT = ConverterService.toDomainFilmFrontPage(page)
      for {
        domainFilmFrontPage <- domainFilmFrontPageT
        filmFrontPage <- filmFrontPageRepository.newFilmFrontPage(domainFilmFrontPage)
      } yield ConverterService.toApiFilmFrontPage(filmFrontPage, None)
    }
  }

}
