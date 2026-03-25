package com.plgdhd.userservice.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    val kafka: KafkaProps = KafkaProps(),
    val cache: CacheProps = CacheProps(),
    val keycloak: KeycloakProps = KeycloakProps()
) {

    data class KafkaProps(
        val topics: TopicsProps = TopicsProps()
    )
    data class TopicsProps(
        val userRegistered: String = "user.registered",
        val userBanned: String = "user.banned",
        val userRoleChanged: String = "user.role.changed"
    )
    data class CacheProps(
        val defaultTtl: Long = 300,
        val cacheNamesTtl: Map<String, Long> = emptyMap()
    )

    data class KeycloakProps(
        val url: String = "",
        val realm: String = "",
        val clientId: String = "",
        val clientSecret: String = ""
    )

}