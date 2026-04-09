package ru.alexandr.notificationservice.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "email_outbox")
class EmailOutboxEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "notification_id", nullable = false)
    val notificationId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: EmailOutboxStatus,

    @Column(name = "attempt_count", nullable = false)
    var attemptCount: Int = 0,

    @Column(name = "next_retry_at")
    var nextRetryAt: LocalDateTime? = null,

    @Column(name = "last_error")
    var lastError: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "processed_at")
    var processedAt: LocalDateTime? = null,
)


enum class EmailOutboxStatus {
    NEW,
    PROCESSING,
    SENT,
    FAILED
}