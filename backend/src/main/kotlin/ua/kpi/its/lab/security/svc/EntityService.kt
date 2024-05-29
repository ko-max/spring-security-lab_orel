package ua.kpi.its.lab.security.svc

import ua.kpi.its.lab.security.dto.JournalRequest
import ua.kpi.its.lab.security.dto.JournalResponse

interface JournalService {
    fun create(journal: JournalRequest): JournalResponse
    fun read(): List<JournalResponse>
    fun readById(id: Long): JournalResponse
    fun updateById(id: Long, journal: JournalRequest): JournalResponse
    fun deleteById(id: Long): JournalResponse
}
