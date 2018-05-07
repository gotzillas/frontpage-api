/*
 * Part of NDLA frontpage_api.
 * Copyright (C) 2018 NDLA
 *
 * See LICENSE
 */

package no.ndla.frontpageapi.model

case class SubjectFrontPageData(twitter: String,
                                facebook: String,
                                banner: String,
                                subjectListLocation: String,
                                mostRead: ArticleCollection,
                                highlights: ArticleCollection,
                                editorsChoices: ArticleCollection,
                                latestContent: ArticleCollection)





