/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi

import no.ndla.frontpageapi.integration.DataSource
import no.ndla.frontpageapi.repository.{FrontPageRepository, SubjectPageRepository}
import no.ndla.frontpageapi.service.{ReadService, WriteService}
import org.scalatest.mockito.MockitoSugar

trait TestEnvironment
    extends MockitoSugar
    with DataSource
    with SubjectPageRepository
    with FrontPageRepository
    with ReadService
    with WriteService {

  override val dataSource = mock[javax.sql.DataSource]

  override val subjectPageRepository = mock[SubjectPageRepository]
  override val frontPageRepository = mock[FrontPageRepository]
  override val readService = mock[ReadService]
  override val writeService = mock[WriteService]
}
