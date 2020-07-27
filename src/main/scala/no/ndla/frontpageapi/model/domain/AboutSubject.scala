/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.model.domain

//TODO er det dumt om languagefield extendes på noe som ikke alle er languagefields? kan jeg endre så det bare er description som er en languagefield?
case class AboutSubject(title: String, description: String, language: String, visualElement: VisualElement)
    extends LanguageField {
  override def isEmpty: Boolean = description.isEmpty
}
