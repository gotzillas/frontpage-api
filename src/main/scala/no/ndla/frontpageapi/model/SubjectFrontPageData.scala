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

case class SubjectFrontPageData(id: Long,
                                twitter: String,
                                facebook: String,
                                banner: String,
                                topical: SubjectTopical,
                                subjectListLocation: String,
                                mostRead: ArticleCollection,
                                editorsChoices: ArticleCollection,
                                latestContent: ArticleCollection)

object SubjectFrontPageData {
  implicit def encoder[F[_]: Applicative]: EntityEncoder[F, SubjectFrontPageData] =
    jsonEncoderOf[F, SubjectFrontPageData](EntityEncoder[F, String],
                                           Applicative[F],
                                           deriveEncoder[SubjectFrontPageData])
  implicit def decoder[F[_]: Sync]: EntityDecoder[F, SubjectFrontPageData] =
    jsonOf[F, SubjectFrontPageData](Sync[F], deriveDecoder[SubjectFrontPageData])
}
