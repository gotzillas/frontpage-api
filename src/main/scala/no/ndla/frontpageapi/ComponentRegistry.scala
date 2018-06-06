/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi

import cats.effect.IO
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import no.ndla.frontpageapi.integration.DataSource
import no.ndla.frontpageapi.repository.{FrontPageRepository, SubjectPageRepository}
import no.ndla.frontpageapi.service.{ReadService, WriteService}
import no.ndla.frontpageapi.FrontpageApiProperties._
import no.ndla.frontpageapi.controller.{FrontPageController, InternController, SubjectPageController}
import scalikejdbc.{ConnectionPool, DataSourceConnectionPool}
import org.http4s.rho.swagger.syntax.{io => ioSwagger}

object ComponentRegistry
    extends DataSource
    with SubjectPageRepository
    with FrontPageRepository
    with InternController
    with ReadService
    with WriteService
    with SubjectPageController
    with FrontPageController {

  val dataSourceConfig = new HikariConfig()
  dataSourceConfig.setUsername(MetaUserName)
  dataSourceConfig.setPassword(MetaPassword)
  dataSourceConfig.setJdbcUrl(s"jdbc:postgresql://$MetaServer:$MetaPort/$MetaResource")
  dataSourceConfig.setSchema(MetaSchema)
  dataSourceConfig.setMaximumPoolSize(MetaMaxConnections)
  override val dataSource = new HikariDataSource(dataSourceConfig)
  ConnectionPool.singleton(new DataSourceConnectionPool(dataSource))

  override val subjectPageRepository = new SubjectPageRepository
  override val frontPageRepository = new FrontPageRepository

  override val readService = new ReadService
  override val writeService = new WriteService

  override val subjectPageController = new SubjectPageController[IO](ioSwagger)
  override val frontPageController = new FrontPageController[IO](ioSwagger)
  override val internController = new InternController[IO](ioSwagger)
}
