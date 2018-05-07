package no.ndla.frontpageapi.controller

import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging
import no.ndla.network.{ApplicationUrl, AuthUser, CorrelationID}
import org.apache.logging.log4j.ThreadContext
import org.http4s.util.CaseInsensitiveString
import org.http4s.{HttpService, Request, Response}

object NdlaMiddleware extends LazyLogging {
  private val CorrelationIdHeader = CaseInsensitiveString("X-Correlation-ID")
  private val CorrelationIdKey = "correlationID"

  private def before(service: HttpService[IO]): HttpService[IO] = cats.data.Kleisli { req: Request[IO] =>
    CorrelationID.set(req.headers.get(CorrelationIdHeader).map(_.value))
    ThreadContext.put(CorrelationIdKey, CorrelationID.get.getOrElse(""))
//    ApplicationUrl.set(req) // TODO
//    AuthUser.set(req) // TODO

    logger.info(s"${req.method} ${req.uri}${req.queryString}")
    service(req)
  }

  private def after(resp: Response[IO]): Response[IO] = {
    CorrelationID.clear()
    ThreadContext.remove(CorrelationIdKey)
    AuthUser.clear()
    ApplicationUrl.clear

    resp
  }

  def apply(service: HttpService[IO]): HttpService[IO] = before(service).map(after)
}
