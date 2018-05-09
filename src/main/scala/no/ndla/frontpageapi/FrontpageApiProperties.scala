/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi

import com.typesafe.scalalogging.LazyLogging

import scala.util.Properties._

object FrontpageApiProperties extends LazyLogging {
  val ApplicationPort: Int = envOrElse("APPLICATION_PORT", "80").toInt
  val ContactName = "Christer Gundersen"
  val ContactEmail = "christergundersen@ndla.no"
}
