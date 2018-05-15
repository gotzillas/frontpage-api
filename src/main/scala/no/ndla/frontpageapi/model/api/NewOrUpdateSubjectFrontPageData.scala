/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.model.api

import cats.effect.Sync
import org.http4s.circe.jsonOf
import org.http4s.EntityDecoder
import scala.language.higherKinds
import io.circe.generic.semiauto._
import io.circe.generic.auto._

case class NewOrUpdateSubjectFrontPageData(twitter: String,
                                           facebook: String,
                                           banner: String,
                                           topical: SubjectTopical,
                                           subjectListLocation: String,
                                           mostRead: ArticleCollection,
                                           editorsChoices: ArticleCollection,
                                           latestContent: ArticleCollection)

object NewOrUpdateSubjectFrontPageData {
  implicit def decoder[F[_]: Sync]: EntityDecoder[F, NewOrUpdateSubjectFrontPageData] =
    jsonOf[F, NewOrUpdateSubjectFrontPageData](Sync[F], deriveDecoder[NewOrUpdateSubjectFrontPageData])
}
