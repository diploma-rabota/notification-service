package ru.alexandr.notificationservice.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.alexandr.notificationservice.entity.EmailOutboxEntity
import ru.alexandr.notificationservice.entity.EmailOutboxStatus
import ru.alexandr.notificationservice.repository.EmailOutboxRepository
import java.time.LocalDateTime

@Service
class EmailOutboxBatchService(
    private val emailOutboxRepository: EmailOutboxRepository,
) {

    @Transactional
    fun claimBatch(limit: Int): List<EmailOutboxEntity> {
        val batch = emailOutboxRepository.findBatchForProcessing(limit)
        val now = LocalDateTime.now()

        batch.forEach {
            it.status = EmailOutboxStatus.PROCESSING
            it.updatedAt = now
        }

        return batch
    }

    @Transactional
    fun markAsSent(outboxId: Long) {
        val outbox = emailOutboxRepository.findById(outboxId)
            .orElseThrow()

        outbox.status = EmailOutboxStatus.SENT
        outbox.processedAt = LocalDateTime.now()
        outbox.updatedAt = LocalDateTime.now()
        outbox.nextRetryAt = null
    }

    @Transactional
    fun markAsFailed(outboxId: Long, maxAttempts: Int, retryDelayMinutes: Long) {
        val outbox = emailOutboxRepository.findById(outboxId)
            .orElseThrow()

        outbox.attemptCount += 1
        outbox.updatedAt = LocalDateTime.now()

        if (outbox.attemptCount >= maxAttempts) {
            outbox.status = EmailOutboxStatus.FAILED
            outbox.nextRetryAt = null
        } else {
            outbox.status = EmailOutboxStatus.NEW
            outbox.nextRetryAt = LocalDateTime.now().plusMinutes(retryDelayMinutes)
        }
    }

    @Transactional
    fun resetStuck(): Int {
        return emailOutboxRepository.resetStuckProcessing()
    }
}