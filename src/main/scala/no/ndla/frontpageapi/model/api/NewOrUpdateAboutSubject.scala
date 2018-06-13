/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.model.api

case class NewOrUpdateAboutSubject(location: Int,
                                   title: String,
                                   description: String,
                                   visualElement: NewOrUpdatedVisualElement)
