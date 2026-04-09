package ru.alexandr.notificationservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.alexandr.notificationservice.entity.NotificationEntity
import java.util.UUID

interface NotificationRepository : JpaRepository<NotificationEntity, Long> {

    fun existsByEventId(eventId: UUID): Boolean
}