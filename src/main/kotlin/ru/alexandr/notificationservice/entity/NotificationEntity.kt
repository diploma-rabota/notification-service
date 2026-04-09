package ru.alexandr.notificationservice.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "notification")
class NotificationEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "event_id", nullable = false, unique = true)
    val eventId: UUID,

    @Column(name = "order_id", nullable = false)
    val orderId: Long,

    @Column(name = "recipient_email", nullable = false)
    val recipientEmail: String,

    @Column(nullable = false)
    var subject: String,

    @Column(nullable = false, columnDefinition = "text")
    var body: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: NotificationStatus,

    @Column(name = "error_message")
    var errorMessage: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "sent_at")
    var sentAt: LocalDateTime? = null,
)


enum class NotificationStatus {
    PENDING,
    SENT,
    FAILED
}