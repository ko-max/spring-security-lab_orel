package ua.kpi.its.lab.security.dto

import kotlinx.serialization.Serializable

@Serializable
data class JournalRequest(
    var name: String,
    var topic: String,
    var language: String,
    var foundationDate: String,
    var issn: String,
    var recommendedPrice: String,
    var periodic: Boolean,
    var article: ArticleRequest
)
@Serializable

data class JournalResponse(
    var id: Long,
    var name: String,
    var topic: String,
    var language: String,
    var foundationDate: String,
    var issn: String,
    var recommendedPrice: String,
    var periodic: Boolean,
    var article: ArticleResponse
)
@Serializable

data class ArticleRequest(
    var title: String,
    var author: String,
    var writingDate: String,
    var wordCount: Int,
    var referenceCount: Int,
    var originalLanguage: Boolean
)
@Serializable

data class ArticleResponse(
    var id: Long,
    var title: String,
    var author: String,
    var writingDate: String,
    var wordCount: Int,
    var referenceCount: Int,
    var originalLanguage: Boolean
)
