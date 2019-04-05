/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi

import no.ndla.network.Domains
import no.ndla.network.secrets.PropertyKeys
import no.ndla.network.secrets.Secrets.readSecrets

import scala.util.Properties._
import scala.util.{Failure, Success}

object FrontpageApiProperties {
  val IsKubernetes: Boolean = envOrNone("NDLA_IS_KUBERNETES").isDefined

  val ApplicationName = "frontpage-api"
  val ApplicationPort: Int = envOrElse("APPLICATION_PORT", "80").toInt
  val ContactName = "Christer Gundersen"
  val ContactEmail = "christergundersen@ndla.no"

  val SecretsFile = "frontpage-api.secrets"

  lazy val MetaUserName: String = prop(PropertyKeys.MetaUserNameKey)
  lazy val MetaPassword: String = prop(PropertyKeys.MetaPasswordKey)
  lazy val MetaResource: String = prop(PropertyKeys.MetaResourceKey)
  lazy val MetaServer: String = prop(PropertyKeys.MetaServerKey)
  lazy val MetaPort: Int = prop(PropertyKeys.MetaPortKey).toInt
  lazy val MetaSchema: String = prop(PropertyKeys.MetaSchemaKey)
  val MetaMaxConnections = 10

  val Environment: String = propOrElse("NDLA_ENVIRONMENT", "local")
  val Domain: String = Domains.get(Environment)
  val RawImageApiUrl: String = s"$Domain/image-api/raw"

  val BrightcoveAccountId: String = prop("BRIGHTCOVE_ACCOUNT")

  lazy val secrets: Map[String, Option[String]] = readSecrets(SecretsFile) match {
    case Success(values)    => values
    case Failure(exception) => throw new RuntimeException(s"Unable to load remote secrets from $SecretsFile", exception)
  }

  def prop(key: String): String = {
    propOrElse(key, throw new RuntimeException(s"Unable to load property $key"))
  }

  def propOrElse(key: String, default: => String): String = {
    envOrNone(key) match {
      case Some(prop)            => prop
      case None if !IsKubernetes => secrets.get(key).flatten.getOrElse(default)
      case _                     => default
    }
  }

}
