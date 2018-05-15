/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.repository

import com.typesafe.scalalogging.LazyLogging
import io.circe.syntax._
import no.ndla.frontpageapi.integration.DataSource
import no.ndla.frontpageapi.model.domain.FrontPageData
import org.postgresql.util.PGobject
import scalikejdbc._
import FrontPageData._

import scala.util.{Failure, Success, Try}

trait FrontPageRepository {
  this: DataSource =>
  val frontPageRepository: FrontPageRepository

  class FrontPageRepository extends LazyLogging {
    private val frontpageId = 1

    def newFrontPage(page: FrontPageData)(implicit session: DBSession = AutoSession): Try[FrontPageData] = {
      val dataObject = new PGobject()
      dataObject.setType("jsonb")
      dataObject.setValue(page.asJson.noSpacesDropNull)

      Try(
        sql"insert into ${FrontPageData.table} (document) values (${dataObject})"
          .updateAndReturnGeneratedKey()
          .apply).map(_ => page)
    }

    def updateFrontPage(page: FrontPageData)(implicit session: DBSession = AutoSession): Try[FrontPageData] = {
      val dataObject = new PGobject()
      dataObject.setType("jsonb")
      dataObject.setValue(page.asJson.noSpacesDropNull)

      Try(sql"update ${FrontPageData.table} set document=${dataObject} where id=${frontpageId}".update.apply)
        .map(_ => page)
    }

    def exists(implicit session: DBSession = AutoSession): Boolean = {
      val result =
        sql"select id from ${FrontPageData.table} where id=${frontpageId}"
          .map(rs => rs.long("id"))
          .single
          .apply()
      result.isDefined
    }

    def get: Option[FrontPageData] = withId(frontpageId)

    private def withId(id: Long): Option[FrontPageData] =
      frontpageWhere(sqls"fr.id=${id.toInt}")

    private def frontpageWhere(whereClause: SQLSyntax)(
        implicit session: DBSession = ReadOnlyAutoSession): Option[FrontPageData] = {
      val fr = FrontPageData.syntax("fr")

      sql"select ${fr.result.*} from ${FrontPageData.as(fr)} where fr.document is not NULL and $whereClause"
        .map(FrontPageData.fromDb(fr))
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
