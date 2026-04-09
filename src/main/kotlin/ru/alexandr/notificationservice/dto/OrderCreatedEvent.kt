package ru.alexandr.notificationservice.dto

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class OrderCreatedEvent(
    val eventId: UUID,
    val orderId: Long,
    val userId: Long,
    val email: String,
    val customerName: String,
    val totalAmount: BigDecimal,
    val createdAt: LocalDateTime,
)