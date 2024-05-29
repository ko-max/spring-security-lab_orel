package ua.kpi.its.lab.security.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import ua.kpi.its.lab.security.util.CustomRSAKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .authorizeHttpRequests {
            it.anyRequest().authenticated()
        }
        .csrf {
            it.ignoringRequestMatchers(AntPathRequestMatcher("/auth/token"))
        }
        .httpBasic(Customizer.withDefaults())
        .oauth2ResourceServer { it.jwt(Customizer.withDefaults())}
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .exceptionHandling {
            it
                .authenticationEntryPoint(BearerTokenAuthenticationEntryPoint())
                .accessDeniedHandler(BearerTokenAccessDeniedHandler())
        }
        .build()

    @Bean
    fun userDetailsService(): UserDetailsService {
        val user = User
            .withUsername("user")
            .password("{noop}password")
            .authorities("application")
            .build()

        return InMemoryUserDetailsManager(user)
    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk = RSAKey
            .Builder(CustomRSAKey.getKeyPair().public as RSAPublicKey)
            .privateKey(CustomRSAKey.getKeyPair().private as RSAPrivateKey)
            .build()
        val jwks = ImmutableJWKSet<SecurityContext>(JWKSet(jwk))
        return NimbusJwtEncoder(jwks)
    }

    @Bean
    fun jwtDecoder(): JwtDecoder = NimbusJwtDecoder
        .withPublicKey(CustomRSAKey.getKeyPair().public as RSAPublicKey)
        .build()
}