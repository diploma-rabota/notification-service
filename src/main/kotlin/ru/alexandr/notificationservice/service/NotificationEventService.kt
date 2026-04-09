package ru.alexandr.notificationservice.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.alexandr.notificationservice.dto.OrderCreatedEvent
import ru.alexandr.notificationservice.entity.EmailOutboxEntity
import ru.alexandr.notificationservice.entity.EmailOutboxStatus
import ru.alexandr.notificationservice.entity.NotificationEntity
import ru.alexandr.notificationservice.entity.NotificationStatus
import ru.alexandr.notificationservice.repository.EmailOutboxRepository
import ru.alexandr.notificationservice.repository.NotificationRepository
import java.time.LocalDateTime

@Service
class NotificationEventService(
    private val notificationRepository: NotificationRepository,
    private val emailOutboxRepository: EmailOutboxRepository,
) {

    @Transactional
    fun handleOrderCreated(event: OrderCreatedEvent) {
        if (notificationRepository.existsByEventId(event.eventId)) {
            return
        }

        val notification = notificationRepository.save(
            NotificationEntity(
                eventId = event.eventId,
                orderId = event.orderId,
                recipientEmail = event.email,
                subject = buildSubject(event),
                body = buildBody(event),
                status = NotificationStatus.PENDING,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )
        )

        emailOutboxRepository.save(
            EmailOutboxEntity(
                notificationId = requireNotNull(notification.id),
                status = EmailOutboxStatus.NEW,
                attemptCount = 0,
                nextRetryAt = null,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                processedAt = null,
            )
        )
    }

    private fun buildSubject(event: OrderCreatedEvent): String {
        return "Заказ №${event.orderId} успешно создан"
    }

    private fun buildBody(event: OrderCreatedEvent): String {
        return """
            Здравствуйте, ${event.customerName}!

            Ваш заказ №${event.orderId} успешно создан.
            Сумма заказа: ${event.totalAmount}
            Дата создания заказа: ${event.createdAt}

            Спасибо за использование нашей платформы.
        """.trimIndent()
    }
}