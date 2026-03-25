package com.plgdhd.userservice.model

import com.plgdhd.userservice.common.UserRole
import com.plgdhd.userservice.common.UserStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_users_username", columnList = "username"),
        Index(name = "idx_users_status", columnList = "status")
    ]
)
public class User(

    @Id
    val id: UUID,

    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    @Column(nullable = false, unique = true, length = 50)
    var username: String,

    @Column(name = "avatar_url")
    var avatarUrl: String? = null,

    @Column(columnDefinition = "TEXT")
    var bio: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var role: UserRole = UserRole.VIEWER,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: UserStatus = UserStatus.ACTIVE,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
){

    @PreUpdate
    fun onUpdate(){
        updatedAt = Instant.now()
    }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is User) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "User(" +
            "id=$id," +
            " email='$email'," +
            " username='$username'," +
            " status=$status" +
            ")"
}