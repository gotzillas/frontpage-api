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
        converted <- ConverterService.toApiSubjectPage(subjectPage, "nb")
      } yield converted
    }

    def updateSubjectPage(id: Long, subject: api.NewSubjectFrontPageData): Try[api.SubjectPageData] = {
      subjectPageRepository.exists(id) match {
        case Success(exists) if exists =>
          for {
            domainSubject <- ConverterService.toDomainSubjectPage(id, subject)
            subjectPage <- subjectPageRepository.updateSubjectPage(domainSubject)
            converted <- ConverterService.toApiSubjectPage(subjectPage, "nb")
          } yield converted
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
            converted <- ConverterService.toApiSubjectPage(subjectPage, subject.about.get.head.language)
          } yield converted
        case None if subjectPageRepository.exists(id).getOrElse(false) =>
          newFromUpdatedSubjectPage(subject) match {
            case Failure(ex)             => Failure(ex)
            case Success(newSubjectPage) => updateSubjectPage(id, newSubjectPage)
          }
        case None =>
          Failure(NotFoundException(404))
      }
    }

    private def newFromUpdatedSubjectPage(
        updatedSubjectPage: api.UpdatedSubjectFrontPageData): Try[api.NewSubjectFrontPageData] = {
      Try(updatedSubjectPage.asInstanceOf[api.NewSubjectFrontPageData])
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
