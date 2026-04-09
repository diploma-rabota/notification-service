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
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

        Сумма заказа: ${formatAmount(event.totalAmount)}
        Дата создания заказа: ${formatDate(event.createdAt)}

        Спасибо за использование нашей платформы!
        
        С уважением,
        Команда Digital Platform
    """.trimIndent()
    }

    private fun formatAmount(amount: BigDecimal): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(amount) + " ₽"
    }
    private fun formatDate(date: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        return date.format(formatter)
    }
}