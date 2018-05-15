/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi

import no.ndla.frontpageapi.integration.DataSource
import no.ndla.frontpageapi.repository.SubjectPageRepository
import no.ndla.frontpageapi.service.{ReadService, WriteService}
import org.scalatest.mockito.MockitoSugar

trait TestEnvironment
    extends MockitoSugar
    with DataSource
    with SubjectPageRepository
    with ReadService
    with WriteService {

  override val dataSource = mock[javax.sql.DataSource]

  override val subjectPageRepository = new SubjectPageRepository
  override val readService = new ReadService
  override val writeService = new WriteService
}
