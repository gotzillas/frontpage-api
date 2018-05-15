/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi

import com.typesafe.scalalogging.LazyLogging
import no.ndla.network.Domains
import no.ndla.network.secrets.PropertyKeys
import no.ndla.network.secrets.Secrets.readSecrets

import scala.util.Properties._
import scala.util.{Failure, Success}

object FrontpageApiProperties extends LazyLogging {
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
  val MetaMaxConnections = 20

  val Environment: String = propOrElse("NDLA_ENVIRONMENT", "local")
  val Domain: String = Domains.get(Environment)
  val RawImageApiUrl: String = s"$Domain/image-api/raw"

  lazy val secrets: Map[String, Option[String]] = readSecrets(SecretsFile) match {
    case Success(values)    => values
    case Failure(exception) => throw new RuntimeException(s"Unable to load remote secrets from $SecretsFile", exception)
  }

  def prop(key: String): String = {
    propOrElse(key, throw new RuntimeException(s"Unable to load property $key"))
  }

  def propOrElse(key: String, default: => String): String = {
    secrets.get(key).flatten match {
      case Some(secret) => secret
      case None =>
        envOrNone(key) match {
          case Some(env) => env
          case None      => default
        }
    }
  }
}
