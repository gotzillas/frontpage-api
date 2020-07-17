/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.model.api

case class NewAboutSubject(title: String,
                           description: String,
                           language: String,
                           visualElement: NewOrUpdatedVisualElement)
