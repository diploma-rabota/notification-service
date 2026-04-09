package ru.alexandr.notificationservice.kafka


import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.alexandr.notificationservice.dto.OrderCreatedEvent
import ru.alexandr.notificationservice.service.NotificationEventService

@Component
class OrderCreatedKafkaListener(
    private val notificationEventService: NotificationEventService,
    private val objectMapper: ObjectMapper,

    ) {
    private val log = KotlinLogging.logger { }

    @KafkaListener(
        topics = ["notification_orders"],
    )
    fun listen(message: String) {
        try {
            val event = objectMapper.readValue(message, OrderCreatedEvent::class.java)
            log.info { "Event received: $event" }
            notificationEventService.handleOrderCreated(event)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}