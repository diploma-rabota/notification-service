package ru.alexandr.notificationservice.email

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
class EmailOutboxExecutorConfig {

    @Bean("emailOutboxExecutor")
    fun emailOutboxExecutor(): Executor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 4
            maxPoolSize = 4
            queueCapacity = 100
            setThreadNamePrefix("email-outbox-")
            setWaitForTasksToCompleteOnShutdown(true)
            setAwaitTerminationSeconds(30)
            initialize()
        }
    }
}