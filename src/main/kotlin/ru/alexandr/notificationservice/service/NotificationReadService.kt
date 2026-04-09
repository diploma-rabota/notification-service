package ru.alexandr.notificationservice.service

import org.springframework.stereotype.Service
import ru.alexandr.notificationservice.entity.NotificationEntity
import ru.alexandr.notificationservice.repository.NotificationRepository

@Service
class NotificationReadService(
    private val notificationRepository: NotificationRepository,
) {
    fun getById(id: Long): NotificationEntity {
        return notificationRepository.findById(id)
            .orElseThrow { IllegalStateException("Notification not found: id=$id") }
    }
}