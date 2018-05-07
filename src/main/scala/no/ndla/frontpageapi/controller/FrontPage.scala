/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

// format: off
package no.ndla.frontpageapi.controller

import cats.effect.Effect
import no.ndla.frontpageapi.model.FrontPageData
import org.http4s.rho.RhoService
import org.http4s.rho.swagger.SwaggerSyntax
import org.http4s.circe._
import io.circe.generic.auto._, io.circe.syntax._
import scala.language.higherKinds

class FrontPage[F[_]: Effect](swaggerSyntax: SwaggerSyntax[F]) extends RhoService[F] {
  import swaggerSyntax._

  "Get data to display on the front page" **
    GET |>> {
      val frontPage = FrontPageData(List(3254, 3501, 7789, 3954), List())
      Ok(frontPage)
  }

}
