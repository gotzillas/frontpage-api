/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.model.domain

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder, HCursor}
import io.circe.generic.auto._
import io.circe.parser._
import no.ndla.frontpageapi.FrontpageApiProperties
import scalikejdbc.WrappedResultSet
import scalikejdbc._
import cats.implicits._

import scala.util.Try

case class SubjectFrontPageData(id: Option[Long],
                                displayInTwoColumns: Boolean,
                                twitter: String,
                                facebook: String,
                                bannerImageId: Long,
                                subjectListLocation: Int,
                                about: AboutSubject,
                                topical: SubjectTopical,
                                mostRead: ArticleCollection,
                                editorsChoices: ArticleCollection,
                                latestContent: ArticleCollection)

object SubjectFrontPageData extends SQLSyntaxSupport[SubjectFrontPageData] {
  override val tableName = "subjectpage"
  override val schemaName = FrontpageApiProperties.MetaSchema.some

  private def getDecoder(id: Long): Decoder[SubjectFrontPageData] =
    (c: HCursor) =>
      for {
        displayInTwoColumns <- c.downField("displayInTwoColumns").as[Boolean]
        twitter <- c.downField("twitter").as[String]
        facebook <- c.downField("facebook").as[String]
        bannerImageId <- c.downField("bannerImageId").as[Long]
        topical <- c.downField("topical").as[SubjectTopical]
        about <- c.downField("about").as[AboutSubject]
        subjectListLocation <- c.downField("subjectListLocation").as[Int]
        mostRead <- c.downField("mostRead").as[ArticleCollection]
        editorsChoices <- c.downField("editorsChoices").as[ArticleCollection]
        latestContent <- c.downField("latestContent").as[ArticleCollection]
      } yield
        SubjectFrontPageData(Some(id),
                             displayInTwoColumns,
                             twitter,
                             facebook,
                             bannerImageId,
                             subjectListLocation,
                             about,
                             topical,
                             mostRead,
                             editorsChoices,
                             latestContent)

  private[domain] def decodeJson(json: String,
                                 id: Long): Try[SubjectFrontPageData] = {
    parse(json).flatMap(_.as[SubjectFrontPageData](getDecoder(id))).toTry
  }

  implicit val encoder: Encoder[SubjectFrontPageData] = deriveEncoder

  def fromDb(lp: SyntaxProvider[SubjectFrontPageData])(
      rs: WrappedResultSet): Try[SubjectFrontPageData] =
    fromDb(lp.resultName)(rs)

  private def fromDb(lp: ResultName[SubjectFrontPageData])(
      rs: WrappedResultSet): Try[SubjectFrontPageData] = {
    val id = rs.long(lp.c("id"))
    val document = rs.string(lp.c("document"))

    decodeJson(document, id)
  }

}
