/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi

import javax.sql.DataSource
import org.flywaydb.core.Flyway

object DBMigrator {

  def migrate(datasource: DataSource): Int = {
    val flyway = new Flyway()
    flyway.setDataSource(datasource)
    flyway.migrate()
  }
}
