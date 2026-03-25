package com.plgdhd.userservice.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.sql.Insert
import java.time.Instant

@Entity
@Table(name = "processed_events")
class ProcessedEvent (

    @Id
    @Column(name = "id", length = 36)
    val id: String,

    @Column(nullable = false, length = 100)
    val topic: String,

    @Column(name = "processed_at", nullable = false)
    val processedAt: Instant = Instant.now(),
)