package ru.alexandr.notificationservice.email

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import ru.alexandr.notificationservice.entity.NotificationEntity

@Component
class EmailSender {

    private val log = KotlinLogging.logger {}

    fun send(notification: NotificationEntity) {
        val threadName = Thread.currentThread().name

        log.info {
            "[$threadName] START send email notificationId=${notification.id}, recipient=${notification.recipientEmail}"
        }

        // Заглушка для демонстрации параллельной работы
        Thread.sleep(2000)

        log.info {
            "[$threadName] FINISH send email notificationId=${notification.id}, recipient=${notification.recipientEmail}"
        }
    }
}


//@Component
//class EmailSender(
//    private val mailSender: JavaMailSender,
//    @Value("\${notification.email.from}")
//    private val from: String,
//) {
//
//    private val log = KotlinLogging.logger {}
//
//    fun send(notification: NotificationEntity) {
//        val thread = Thread.currentThread().name
//
//        log.info { "[$thread] Sending email to ${notification.recipientEmail}" }
//
//        val message = SimpleMailMessage().apply {
//            from = from
//            setTo(notification.recipientEmail)
//            subject = notification.subject
//            text = notification.body
//        }
//
//        mailSender.send(message)
//
//        log.info { "[$thread] Email sent to ${notification.recipientEmail}" }
//    }
//}