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

case class NewSubjectFrontPageData(name: String,
                                   filters: Option[List[String]],
                                   externalId: String,
                                   layout: String,
                                   twitter: Option[String],
                                   facebook: Option[String],
                                   bannerImage: NewOrUpdateBannerImage,
                                   about: Seq[NewOrUpdatedAboutSubject],
                                   metaDescription: Seq[NewOrUpdatedMetaDescription],
                                   topical: Option[String],
                                   mostRead: List[String],
                                   editorsChoices: List[String],
                                   latestContent: Option[List[String]],
                                   goTo: List[String])

object NewSubjectFrontPageData {
  implicit def decoder[F[_]: Sync]: EntityDecoder[F, NewSubjectFrontPageData] =
    jsonOf[F, NewSubjectFrontPageData](Sync[F], deriveDecoder[NewSubjectFrontPageData])
}
