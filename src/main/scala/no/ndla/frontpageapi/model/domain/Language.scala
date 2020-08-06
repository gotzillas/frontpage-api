package no.ndla.frontpageapi.model.domain

import no.ndla.mapping.ISO639

object Language {
  val DefaultLanguage = "nb"
  val UnknownLanguage = "unknown"
  val NoLanguage = ""
  val AllLanguages = "all"

  def getSupportedLanguages(sequences: Seq[Seq[LanguageField]]): Seq[String] = {
    sequences.flatMap(_.map(_.language)).distinct.sortBy { lang =>
      ISO639.languagePriority.indexOf(lang)
    }
  }

  def findByLanguageOrBestEffort[P <: LanguageField](sequence: Seq[P], language: String): Option[P] = {
    sequence
      .find(_.language == language)
      .orElse(sequence.sortBy(lf => ISO639.languagePriority.reverse.indexOf(lf.language)).lastOption)
  }
}
