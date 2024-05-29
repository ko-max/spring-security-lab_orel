package ua.kpi.its.lab.security.svc.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ua.kpi.its.lab.security.dto.ArticleResponse
import ua.kpi.its.lab.security.dto.JournalRequest
import ua.kpi.its.lab.security.dto.JournalResponse
import ua.kpi.its.lab.security.entity.Article
import ua.kpi.its.lab.security.entity.Journal
import ua.kpi.its.lab.security.repo.JournalRepository
import ua.kpi.its.lab.security.svc.JournalService
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class JournalServiceImpl @Autowired constructor(
    private val repository: JournalRepository
) : JournalService {
    override fun create(journal: JournalRequest): JournalResponse {
        val article=journal.article
        val newArticle = Article(
            title = journal.article.title,
            author = journal.article.author,
            writingDate = stringToDate(journal.article.writingDate),
            wordCount = journal.article.wordCount,
            referenceCount = journal.article.referenceCount,
            originalLanguage = journal.article.originalLanguage,
            journal = null // This will be set later
        )
        var newJournal = Journal(
            name = journal.name,
            topic = journal.topic,
            language = journal.language,
            foundationDate = stringToDate(journal.foundationDate),
            issn = journal.issn,
            recommendedPrice = journal.recommendedPrice,
            periodic = journal.periodic,
            article = newArticle
        )
        newArticle.journal = newJournal
        newJournal = this.repository.save(newJournal)
        return this.journalEntityToDto(newJournal)
    }

    override fun read(): List<JournalResponse> {
        return this.repository.findAll().map(this::journalEntityToDto)
    }

    override fun readById(id: Long): JournalResponse {
        val journal = this.getJournalById(id)
        return this.journalEntityToDto(journal)
    }

    override fun updateById(id: Long, journal: JournalRequest): JournalResponse {
        val oldJournal = this.getJournalById(id)
        val article=journal.article
        val newArticle = Article(
            title = journal.article.title,
            author = journal.article.author,
            writingDate = stringToDate(journal.article.writingDate),
            wordCount = journal.article.wordCount,
            referenceCount = journal.article.referenceCount,
            originalLanguage = journal.article.originalLanguage,
            journal = oldJournal
        )
        oldJournal.apply {
            name = journal.name
            topic = journal.topic
            language = journal.language
            foundationDate = stringToDate(journal.foundationDate)
            issn = journal.issn
            recommendedPrice = journal.recommendedPrice
            periodic = journal.periodic
        }
        val updatedJournal = this.repository.save(oldJournal)
        return this.journalEntityToDto(updatedJournal)
    }

    override fun deleteById(id: Long): JournalResponse {
        val journal = this.getJournalById(id)
        this.repository.delete(journal)
        return this.journalEntityToDto(journal)
    }

    private fun getJournalById(id: Long): Journal {
        return this.repository.findById(id).orElseThrow {
            IllegalArgumentException("Journal not found by id = $id")
        }
    }

    private fun journalEntityToDto(journal: Journal): JournalResponse {
        return JournalResponse(
            id = journal.id,
            name = journal.name,
            topic = journal.topic,
            language = journal.language,
            foundationDate = dateToString(journal.foundationDate),
            issn = journal.issn,
            recommendedPrice = journal.recommendedPrice,
            periodic = journal.periodic,
            article = this.articleEntityToDto(journal.article)
        )
    }

    private fun articleEntityToDto(article: Article): ArticleResponse {
        return ArticleResponse(
            id = article.id,
            title = article.title,
            author = article.author,
            writingDate = dateToString(article.writingDate),
            wordCount = article.wordCount,
            referenceCount = article.referenceCount,
            originalLanguage = article.originalLanguage
        )
    }

    private fun dateToString(date: Date): String {
        val instant = date.toInstant()
        val dateTime = instant.atOffset(ZoneOffset.UTC).toLocalDateTime()
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME)
    }

    private fun stringToDate(date: String): Date {
        return try {
            val dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
            val instant = dateTime.toInstant(ZoneOffset.UTC)
            Date.from(instant)
        } catch (e: Exception) {
            Date() // Return current date as fallback
        }
    }
}
