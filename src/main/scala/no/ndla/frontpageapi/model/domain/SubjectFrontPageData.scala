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
import io.circe.generic.extras.Configuration

import scala.util.Try

case class SubjectFrontPageData(id: Option[Long],
                                name: String,
                                displayInTwoColumns: Boolean,
                                twitter: String,
                                facebook: String,
                                bannerImageId: Long,
                                subjectListLocation: Int,
                                about: AboutSubject,
                                topical: SubjectTopical,
                                mostRead: ArticleCollection,
                                editorsChoices: ArticleCollection,
                                latestContent: ArticleCollection,
                                goTo: GoToCollection)

object SubjectFrontPageData extends SQLSyntaxSupport[SubjectFrontPageData] {
  override val tableName = "subjectpage"
  override val schemaName = FrontpageApiProperties.MetaSchema.some

  implicit val enumDecoder = Decoder.enumDecoder(VisualElementType)
  implicit val enumEncoder = Encoder.enumEncoder(VisualElementType)

  private[domain] def decodeJson(json: String, id: Long): Try[SubjectFrontPageData] = {
    parse(json).flatMap(_.as[SubjectFrontPageData]).map(_.copy(id = id.some)).toTry
  }

  def fromDb(lp: SyntaxProvider[SubjectFrontPageData])(rs: WrappedResultSet): Try[SubjectFrontPageData] =
    fromDb(lp.resultName)(rs)

  private def fromDb(lp: ResultName[SubjectFrontPageData])(rs: WrappedResultSet): Try[SubjectFrontPageData] = {
    val id = rs.long(lp.c("id"))
    val document = rs.string(lp.c("document"))

    decodeJson(document, id)
  }

}
