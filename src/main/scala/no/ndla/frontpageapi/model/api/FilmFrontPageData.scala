/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2019 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.model.api

import cats.Applicative
import cats.effect.Sync
import io.circe._
import io.circe.generic.semiauto._
import io.circe.generic.auto._
import org.http4s.circe.{jsonEncoderWithPrinterOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

import scala.language.higherKinds

case class FilmFrontPageData(name: String,
                             about: Option[AboutSubject],
                             movieThemes: Seq[MovieTheme],
                             slideShow: Seq[String])

object FilmFrontPageData {
  val indentDropNull = Printer.spaces2.copy(dropNullValues = true)

  implicit def encoder[F[_]: Applicative]: EntityEncoder[F, FilmFrontPageData] =
    jsonEncoderWithPrinterOf[F, FilmFrontPageData](indentDropNull)(EntityEncoder[F, String],
                                                                   Applicative[F],
                                                                   deriveEncoder[FilmFrontPageData])

  implicit def decoder[F[_]: Sync]: EntityDecoder[F, FilmFrontPageData] =
    jsonOf[F, FilmFrontPageData](Sync[F], deriveDecoder[FilmFrontPageData])
}
