package com.plgdhd.userservice.repository

import com.plgdhd.userservice.common.UserRole
import com.plgdhd.userservice.common.UserStatus
import com.plgdhd.userservice.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID>{

    fun findByUsername(username: String): User?

    fun existsByEmail(email: String) : Boolean

    @Modifying
    @Query("UPDATE User u SET u.status = :status, u.updatedAt = :now WHERE u.id = :id")
    fun updateStaus(id: UUID, status: UserStatus, updatedAt: Instant = Instant.now())

    @Modifying
    @Query("UPDATE User u SET u.role = :role, u.updatedAt = :now WHERE u.id = :id")
    fun updateRole(id: UUID, role: UserRole, updatedAt: Instant = Instant.now())
}