package ua.kpi.its.lab.security.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.web.bind.annotation.*
import ua.kpi.its.lab.security.dto.JournalRequest
import ua.kpi.its.lab.security.dto.JournalResponse
import ua.kpi.its.lab.security.svc.JournalService
import java.time.Instant

@RestController
@RequestMapping("/journals")
class JournalController @Autowired constructor(
    private val journalService: JournalService
) {
    @GetMapping(path = ["", "/"])
    fun journals(): List<JournalResponse> = journalService.read()

    @GetMapping("{id}")
    fun readJournal(@PathVariable("id") id: Long): ResponseEntity<JournalResponse> {
        return wrapNotFound { journalService.readById(id) }
    }

    @PostMapping(path = ["", "/"])
    fun createJournal(@RequestBody journal: JournalRequest): JournalResponse {
        return journalService.create(journal)
    }

    @PutMapping("{id}")
    fun updateJournal(
        @PathVariable("id") id: Long,
        @RequestBody journal: JournalRequest
    ): ResponseEntity<JournalResponse> {
        return wrapNotFound { journalService.updateById(id, journal) }
    }

    @DeleteMapping("{id}")
    fun deleteJournal(
        @PathVariable("id") id: Long
    ): ResponseEntity<JournalResponse> {
        return wrapNotFound { journalService.deleteById(id) }
    }

    fun <T> wrapNotFound(call: () -> T): ResponseEntity<T> {
        return try {
            val result = call()
            ResponseEntity.ok(result)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }
}
@RestController
@RequestMapping("/auth")
class AuthenticationTokenController @Autowired constructor(
    private val encoder: JwtEncoder
) {
    private val authTokenExpiry: Long = 3600L // in seconds

    @PostMapping("token")
    fun token(auth: Authentication): String {
        val now = Instant.now()
        val scope = auth
            .authorities
            .joinToString(" ", transform = GrantedAuthority::getAuthority)
        val claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(authTokenExpiry))
            .subject(auth.name)
            .claim("scope", scope)
            .build()
        return encoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }
}
