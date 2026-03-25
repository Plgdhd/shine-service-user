package com.plgdhd.userservice.repository

import com.plgdhd.userservice.model.ProcessedEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface ProcessedEventRepository : JpaRepository<ProcessedEvent, String> {

    @Modifying
    @Query("DELETE FROM ProcessedEvent pe WHERE pe.processedAt < :cutoff")
    fun deleteOlderThan(cutoff: Instant): Int
}