/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2019 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.model.api

import io.circe.generic.auto._

import scala.language.higherKinds

case class FilmFrontPageData(name: String,
                             about: Seq[AboutFilmSubject],
                             movieThemes: Seq[MovieTheme],
                             slideShow: Seq[String])
