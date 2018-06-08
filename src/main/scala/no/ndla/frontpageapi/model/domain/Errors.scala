/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.model.domain

object Errors {
  case class NotFoundException(id: Long) extends RuntimeException(s"The page with id $id was not found")
  case class ValidationException(message: String) extends RuntimeException(message)
}
