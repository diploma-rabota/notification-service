package ru.alexandr.notificationservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.alexandr.notificationservice.entity.EmailOutboxEntity
import ru.alexandr.notificationservice.entity.EmailOutboxStatus
import java.time.LocalDateTime

interface EmailOutboxRepository : JpaRepository<EmailOutboxEntity, Long> {

    fun findTop10ByStatusOrderByCreatedAtAsc(
        status: EmailOutboxStatus
    ): List<EmailOutboxEntity>

    fun findTop10ByStatusAndNextRetryAtBeforeOrderByCreatedAtAsc(
        status: EmailOutboxStatus,
        time: LocalDateTime
    ): List<EmailOutboxEntity>
}