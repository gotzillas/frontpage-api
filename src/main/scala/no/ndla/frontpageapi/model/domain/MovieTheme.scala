/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2019 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.model.domain

case class MovieTheme(id: Long, name: Seq[MovieName], movies: Seq[String])

case class MovieName(name: String, language: String)
