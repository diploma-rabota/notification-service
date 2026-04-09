package ru.alexandr.notificationservice.email


import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.alexandr.notificationservice.service.EmailOutboxBatchService
import ru.alexandr.notificationservice.service.NotificationReadService
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor


private val log = KotlinLogging.logger {}

@Component
class EmailOutboxScheduler(
    private val emailOutboxBatchService: EmailOutboxBatchService,
    private val notificationReadService: NotificationReadService,
    private val emailSender: EmailSender,
    @Qualifier("emailOutboxExecutor")
    private val emailOutboxExecutor: Executor,
    @Value("\${notification.email.batch-size:10}")
    private val batchSize: Int,
    @Value("\${notification.email.max-attempts:3}")
    private val maxAttempts: Int,
    @Value("\${notification.email.retry-delay-minutes:1}")
    private val retryDelayMinutes: Long,
) {

    @Scheduled(fixedDelayString = "\${notification.email.scheduler-delay-ms:5000}")
    fun processEmailOutbox() {
        val schedulerThread = Thread.currentThread().name
        log.info { "[$schedulerThread] Email outbox scheduler started" }

        val resetCount = emailOutboxBatchService.resetStuck()
        if (resetCount > 0) {
            log.warn { "[$schedulerThread] Reset $resetCount stuck email outbox records" }
        }

        val batch = emailOutboxBatchService.claimBatch(batchSize)
        if (batch.isEmpty()) {
            log.debug { "[$schedulerThread] No email outbox records to process" }
            return
        }

        log.info { "[$schedulerThread] Claimed ${batch.size} email outbox records for processing" }

        val futures = batch.map { outbox ->
            CompletableFuture.runAsync(
                {
                    val workerThread = Thread.currentThread().name
                    val outboxId = requireNotNull(outbox.id)

                    try {
                        log.info {
                            "[$workerThread] START processing outboxId=$outboxId, notificationId=${outbox.notificationId}"
                        }

                        val notification = notificationReadService.getById(outbox.notificationId)
                        emailSender.send(notification)

                        emailOutboxBatchService.markAsSent(outboxId)

                        log.info {
                            "[$workerThread] SUCCESS processing outboxId=$outboxId, notificationId=${outbox.notificationId}"
                        }
                    } catch (e: Exception) {
                        log.error(e) {
                            "[$workerThread] ERROR processing outboxId=$outboxId, notificationId=${outbox.notificationId}"
                        }

                        emailOutboxBatchService.markAsFailed(
                            outboxId = outboxId,
                            maxAttempts = maxAttempts,
                            retryDelayMinutes = retryDelayMinutes,
                        )
                    }
                },
                emailOutboxExecutor
            )
        }

        CompletableFuture.allOf(*futures.toTypedArray()).join()

        log.info { "[$schedulerThread] Email outbox scheduler finished batch of size ${batch.size}" }
    }
}