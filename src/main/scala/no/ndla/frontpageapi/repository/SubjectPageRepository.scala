/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.repository

import no.ndla.frontpageapi.integration.DataSource
import no.ndla.frontpageapi.model.domain.SubjectFrontPageData
import com.typesafe.scalalogging.LazyLogging
import org.postgresql.util.PGobject
import scalikejdbc._
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.parser._
import SubjectFrontPageData._
import scala.util.{Failure, Success, Try}

trait SubjectPageRepository {
  this: DataSource =>
  val subjectPageRepository: SubjectPageRepository

  class SubjectPageRepository extends LazyLogging {

    def newSubjectPage(subj: SubjectFrontPageData, externalId: String)(
        implicit session: DBSession = AutoSession): Try[SubjectFrontPageData] = {
      val dataObject = new PGobject()
      dataObject.setType("jsonb")
      dataObject.setValue(subj.copy(id = None).asJson.noSpacesDropNull)

      Try(
        sql"insert into ${SubjectFrontPageData.table} (document, external_id) values (${dataObject}, ${externalId})"
          .updateAndReturnGeneratedKey()
          .apply).map(id => {
        logger.info(s"Inserted new subject page: $id")
        subj.copy(id = Some(id))
      })
    }

    def updateSubjectPage(subj: SubjectFrontPageData)(
        implicit session: DBSession = AutoSession): Try[SubjectFrontPageData] = {
      val dataObject = new PGobject()
      dataObject.setType("jsonb")
      dataObject.setValue(subj.copy(id = None).asJson.noSpacesDropNull)

      Try(sql"update ${SubjectFrontPageData.table} set document=${dataObject} where id=${subj.id}".update.apply)
        .map(_ => subj)
    }

    def withId(subjectId: Long): Option[SubjectFrontPageData] =
      subjectPageWhere(sqls"su.id=${subjectId.toInt}")

    def getIdFromExternalId(externalId: String)(implicit sesstion: DBSession = AutoSession): Option[Long] = {
      sql"select id from ${SubjectFrontPageData.table} where external_id=${externalId}"
        .map(rs => rs.long("id"))
        .single
        .apply()
    }

    def exists(subjectId: Long)(implicit sesstion: DBSession = AutoSession): Boolean = {
      val result =
        sql"select id from ${SubjectFrontPageData.table} where id=${subjectId}".map(rs => rs.long("id")).single.apply()
      result.isDefined
    }

    private def subjectPageWhere(whereClause: SQLSyntax)(
        implicit session: DBSession = ReadOnlyAutoSession): Option[SubjectFrontPageData] = {
      val su = SubjectFrontPageData.syntax("su")

      sql"select ${su.result.*} from ${SubjectFrontPageData.as(su)} where su.document is not NULL and $whereClause"
        .map(SubjectFrontPageData.fromDb(su))
        .single
        .apply() match {
        case Some(Success(s)) => Some(s)
        case Some(Failure(ex)) =>
          ex.printStackTrace()
          None
        case None => None
      }
    }

  }
}
