package ua.kpi.its.lab.security.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "journals")
class Journal(
    @Column
    var name: String,

    @Column
    var topic: String,

    @Column
    var language: String,

    @Column
    var foundationDate: Date,

    @Column
    var issn: String,

    @Column
    var recommendedPrice: String,

    @Column
    var periodic: Boolean,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "article_id", referencedColumnName = "id")
    var article: Article
) : Comparable<Journal> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = -1

    override fun compareTo(other: Journal): Int {
        val equal = this.name == other.name && this.foundationDate.time == other.foundationDate.time
        return if (equal) 0 else 1
    }

    override fun toString(): String {
        return "Journal(name=$name, foundationDate=$foundationDate, article=$article)"
    }
}

@Entity
@Table(name = "articles")
class Article(
    @Column
    var title: String,

    @Column
    var author: String,

    @Column
    var writingDate: Date,

    @Column
    var wordCount: Int,

    @Column
    var referenceCount: Int,

    @Column
    var originalLanguage: Boolean,

    @OneToOne(mappedBy = "article")
    var journal: Journal? = null
) : Comparable<Article> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = -1

    override fun compareTo(other: Article): Int {
        val equal = this.title == other.title && this.writingDate.time == other.writingDate.time
        return if (equal) 0 else 1
    }

    override fun toString(): String {
        return "Article(title=$title, writingDate=$writingDate)"
    }
}
