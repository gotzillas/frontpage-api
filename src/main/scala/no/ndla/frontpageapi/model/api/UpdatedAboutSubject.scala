package no.ndla.frontpageapi.model.api

import no.ndla.frontpageapi.model.domain.LanguageField

//TODO er det dumt om languagefield extendes på noe som ikke alle er languagefields? kan jeg endre så det bare er description som er en languagefield?
case class UpdatedAboutSubject( title: String,
                                description: String,
                                language: String,
                                visualElement: NewOrUpdatedVisualElement) extends LanguageField {
  override def isEmpty: Boolean = description.isEmpty

}
