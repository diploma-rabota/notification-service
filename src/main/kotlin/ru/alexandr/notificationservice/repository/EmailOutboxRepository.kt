package ru.alexandr.notificationservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import ru.alexandr.notificationservice.entity.EmailOutboxEntity
import ru.alexandr.notificationservice.entity.EmailOutboxStatus
import java.time.LocalDateTime


interface EmailOutboxRepository : JpaRepository<EmailOutboxEntity, Long> {

    @Query(
        value = """
            select *
            from email_outbox eo
            where eo.status = 'NEW'
              and (eo.next_retry_at is null or eo.next_retry_at <= now())
            order by eo.created_at
            limit :limit
            for update skip locked
        """,
        nativeQuery = true
    )
    fun findBatchForProcessing(limit: Int): List<EmailOutboxEntity>

    fun findByNotificationId(notificationId: Long): EmailOutboxEntity?

    @Modifying
    @Query(
        value = """
            update email_outbox
            set status = 'NEW',
                updated_at = now()
            where status = 'PROCESSING'
              and updated_at < now() - interval '5 minutes'
        """,
        nativeQuery = true
    )
    fun resetStuckProcessing(): Int
}