// format: off
package no.ndla.frontpageapi.controller

import cats.effect.Effect
import no.ndla.frontpageapi.model.{ArticleCollection, SubjectFrontPageData}
import org.http4s.rho.RhoService
import org.http4s.rho.swagger.SwaggerSyntax
import org.http4s.circe._
import io.circe.generic.auto._, io.circe.syntax._
import scala.language.higherKinds


class SubjectPage[F[_]: Effect](swaggerSyntax: SwaggerSyntax[F])
    extends RhoService[F] {
  import swaggerSyntax._


  "Get data to display on a subject page" **
    GET / pathVar[Int] |>> { id: Int =>
      val mockSubjectPage = SubjectFrontPageData(
        "samfunnsfag",
        "@samfunnsfag",
        "https://test.api.ndla.no/image-api/v2/images/29668",
        "top",
        ArticleCollection("top", List(3254, 3501, 7789, 3954)),
        ArticleCollection("top", List(3254, 3501, 7789, 3954)),
        ArticleCollection("top", List(3254, 3501, 7789, 3954)),
        ArticleCollection("top", List(3254, 3501, 7789, 3954))
      ).asJson

      Ok(mockSubjectPage)
    }

}
