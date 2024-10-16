package com.studets.ingsispermission.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class OAuth2ResourceServerSecurityConfiguration(@Value("\${auth0.audience}")
                                                val audience: String,
                                                @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
                                                val issuer: String,) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
            it
                .requestMatchers(POST, "/api/user").hasAuthority("SCOPE_read:snippets")
                .requestMatchers(GET, "/snippets/*").hasAuthority("SCOPE_read:snippets")
                .requestMatchers(POST, "/snippets").hasAuthority("SCOPE_write:snippets")
                .anyRequest().authenticated()
        }
            .oauth2ResourceServer { it.jwt(withDefaults()) }
            .cors { it.disable() }
            .csrf { it.disable() }
        return http.build()
    }
}