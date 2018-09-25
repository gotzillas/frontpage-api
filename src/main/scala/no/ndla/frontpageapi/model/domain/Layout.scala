package no.ndla.frontpageapi.model.domain
import no.ndla.frontpageapi.model.domain.Errors.ValidationException

import scala.util.{Failure, Success, Try}

/**
  * Part of GDL frontpage-api.
  * Copyright (C) 2018 GDL
  *
  * See LICENSE
  */
case class Layout(`type`: LayoutType.Value)

object LayoutType extends Enumeration {
  val Single: LayoutType.Value = Value("single")
  val Double: LayoutType.Value = Value("double")
  val Stacked: LayoutType.Value = Value("stacked")

  def fromString(string: String): Try[LayoutType.Value] =
    LayoutType.values.find(_.toString == string) match {
      case Some(v) => Success(v)
      case None    => Failure(ValidationException(s"'$string' is an invalid visual element type"))
    }
}
