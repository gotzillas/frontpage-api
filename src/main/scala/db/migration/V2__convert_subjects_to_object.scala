package db.migration
import java.sql.Connection

import io.circe.generic.semiauto._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import no.ndla.frontpageapi.model.domain.{FrontPageData, SubjectCollection, SubjectFilters}
import no.ndla.frontpageapi.repository._
import org.flywaydb.core.api.migration.jdbc.JdbcMigration
import org.postgresql.util.PGobject
import scalikejdbc._

/**
 * Part of GDL frontpage-api.
 * Copyright (C) 2018 GDL
 *
 * See LICENSE
 */
class V2__convert_subjects_to_object extends JdbcMigration {

  implicit val decoder: Decoder[V2_DBFrontPageData] = deriveDecoder
  implicit val encoder: Encoder[V2_DBFrontPageData] = deriveEncoder

  override def migrate(connection: Connection) : Unit = {
    val db = DB(connection)
    db.autoClose(false)
    db.withinTx { implicit session =>
      frontPageData.map(convertSubjects).foreach(update)
    }
  }

  def frontPageData(implicit session: DBSession) : Option[V2_DBFrontPage] = {
    sql"select id, document from mainfrontpage"
      .map(rs => V2_DBFrontPage(rs.long("id"), rs.string("document")))
      .single
      .apply
  }

  def convertSubjects(frontPage: V2_DBFrontPage): FrontPageData = {
    parse(frontPage.document).flatMap(_.as[V2_DBFrontPageData]).toTry match {
      case scala.util.Success(frontPage) => {
        new FrontPageData(frontPage.topical, toDomainCategories(frontPage.categories))
      }
    }
  }

  private def toDomainCategories(dbCategories: List[V2_DBSubjectCollection]) : List[SubjectCollection] = {
    dbCategories.map(sc => SubjectCollection(sc.name, sc.subjects.map(s => SubjectFilters(s, List()))))
  }

  def update(frontPageData: FrontPageData)(implicit session: DBSession) = {
    val dataObject = new PGobject()
    dataObject.setType("jsonb")
    dataObject.setValue(frontPageData.asJson.noSpacesDropNull)

    sql"update mainfrontpage set document = $dataObject"
      .update()
      .apply()
  }
}

case class V2_DBFrontPage(id: Long, document: String)
case class V2_DBFrontPageData(topical: List[String], categories: List[V2_DBSubjectCollection])
case class V2_DBSubjectCollection(name: String, subjects: List[String])
