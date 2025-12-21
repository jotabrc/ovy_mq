package io.github.jotabrc.ovy_mq_client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

    public static final String LISTENER_EXECUTOR = "listenerExecutor";
    public static final String SCHEDULED_EXECUTOR = "scheduledExecutor";
    public static final String SCHEDULED_SHUTDOWN_EXECUTOR = "scheduledShutdownExecutor";
    public static final String SCHEDULED_BACKOFF_EXECUTOR = "scheduledBackoffExecutor";
    public static final String PRODUCER_EXECUTOR = "producerExecutor";

    @Value("${ovy.executor.client-task.core-pool-size:5}")
    private Integer clientTaskCorePoolSize;

    @Value("${ovy.executor.client-task.shutdown.core-pool-size:3}")
    private Integer clientTaskShutdownCorePoolSize;

    @Value("${ovy.executor.client-task.core-pool-size:5}")
    private Integer backoffTaskCorePoolSize;

    @Value("${ovy.executor.listener.core-pool-size:5}")
    private Integer listenerExecutorCorePoolSize;
    @Value("${ovy.executor.listener-task.max-pool-size:25}")
    private Integer listenerExecutorMaxPoolSize;
    @Value("${ovy.executor.listener-task.queue-capacity:1000}")
    private Integer listenerExecutorQueueCapacity;

    @Value("${ovy.executor.producer.core-pool-size:5}")
    private Integer producerExecutorCorePoolSize;
    @Value("${ovy.executor.producer-task.max-pool-size:25}")
    private Integer producerExecutorMaxPoolSize;
    @Value("${ovy.executor.producer-task.queue-capacity:1000}")
    private Integer producerExecutorQueueCapacity;

    @Bean(name = SCHEDULED_EXECUTOR)
    public ScheduledExecutorService scheduledExecutor() {
        return new ScheduledThreadPoolExecutor(clientTaskCorePoolSize, r -> {
            Thread thread = new Thread(r, "ClientTaskExecutor");
            thread.setDaemon(true);
            return thread;
        });
    }

    @Bean(name = SCHEDULED_BACKOFF_EXECUTOR)
    public ScheduledExecutorService scheduledBackoffExecutor() {
        return new ScheduledThreadPoolExecutor(clientTaskCorePoolSize, r -> {
            Thread thread = new Thread(r, "BackoffTaskExecutor");
            thread.setDaemon(true);
            return thread;
        });
    }

    @Bean(name = SCHEDULED_SHUTDOWN_EXECUTOR)
    public ScheduledExecutorService scheduledShutdownExecutor() {
        return new ScheduledThreadPoolExecutor(clientTaskShutdownCorePoolSize, r -> {
            Thread thread = new Thread(r, "ClientShutdownExecutor");
            thread.setDaemon(true);
            return thread;
        });
    }

    @Bean(name = PRODUCER_EXECUTOR)
    public Executor producerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(producerExecutorCorePoolSize);
        executor.setMaxPoolSize(producerExecutorMaxPoolSize);
        executor.setQueueCapacity(producerExecutorQueueCapacity);
        executor.setThreadNamePrefix("ProducerExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean(name = LISTENER_EXECUTOR)
    public Executor listenerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(listenerExecutorCorePoolSize);
        executor.setMaxPoolSize(listenerExecutorMaxPoolSize);
        executor.setQueueCapacity(listenerExecutorQueueCapacity);
        executor.setThreadNamePrefix("ListenerExecutor-");
        executor.initialize();
        return executor;
    }
}
