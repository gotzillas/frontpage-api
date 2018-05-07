/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.model

import cats.Applicative
import cats.effect.Sync
import io.circe.generic.semiauto._
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import io.circe.generic.auto._
import io.circe._

case class FrontPageData(topical: List[Long], subjects: List[SubjectCollection])

object FrontPageData {
  implicit def encoder[F[_]: Applicative]: EntityEncoder[F, FrontPageData] =
    jsonEncoderOf[F, FrontPageData](EntityEncoder[F, String], Applicative[F], deriveEncoder[FrontPageData])
  implicit def decoder[F[_]: Sync]: EntityDecoder[F, FrontPageData] =
    jsonOf[F, FrontPageData](Sync[F], deriveDecoder[FrontPageData])
}
