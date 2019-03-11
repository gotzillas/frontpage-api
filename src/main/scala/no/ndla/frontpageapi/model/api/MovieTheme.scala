/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2019 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.model.api

case class MovieTheme(id: Long, name: Option[String], movies: Seq[String])
