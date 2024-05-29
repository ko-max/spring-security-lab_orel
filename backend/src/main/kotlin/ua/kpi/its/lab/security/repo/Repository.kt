package ua.kpi.its.lab.security.repo

import org.springframework.data.jpa.repository.JpaRepository
import ua.kpi.its.lab.security.entity.Article
import ua.kpi.its.lab.security.entity.Journal

interface JournalRepository : JpaRepository<Journal, Long>

interface ArticleRepository : JpaRepository<Article, Long>
