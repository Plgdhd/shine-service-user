package com.plgdhd.userservice.config

import com.plgdhd.userservice.common.UserRole
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtAuthenticationConverter: JwtAuthenticationConverter): SecurityFilterChain {

        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(HttpMethod.GET, "/api/v1/users/*/profile").permitAll()
                auth.requestMatchers( "/actuator/health").permitAll()

                auth.anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt() { jwt ->
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                }
            }
        return http.build()
    }

     @Bean
     fun jwtAuthenticationConverter(): JwtAuthenticationConverter {

         val converter = JwtAuthenticationConverter()
         converter.setJwtGrantedAuthoritiesConverter { jwt: Jwt ->

             @Suppress("UNCHEKED_CAST")
             val roles = (jwt.getClaimAsMap("realm_access")?.get("roles") as? List<String>) ?: emptyList()
             roles
                 .filter { !it.startsWith("default-roles-") && it !in listOf("offline_access", "uma_authorization") }
                 .map { SimpleGrantedAuthority("ROLE_$it") }
         }
         converter.setPrincipalClaimName("sub")
         return converter
     }
}