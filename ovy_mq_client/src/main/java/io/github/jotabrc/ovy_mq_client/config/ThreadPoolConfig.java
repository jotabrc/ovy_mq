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

    @Value("${ovy.executor.client-task.core-pool-size:1}")
    private Integer clientTaskCorePoolSize;

    @Value("${ovy.executor.listener.core-pool-size:3}")
    private Integer listenerExecutorCorePoolSize;
    @Value("${ovy.executor.client-task.max-pool-size:10}")
    private Integer listenerExecutorMaxPoolSize;
    @Value("${ovy.executor.client-task.queue-capacity:25}")
    private Integer listenerExecutorQueueCapacity;

    @Bean(name = SCHEDULED_EXECUTOR)
    public ScheduledExecutorService scheduledExecutor() {
        return new ScheduledThreadPoolExecutor(clientTaskCorePoolSize, r -> {
            Thread thread = new Thread(r, "ClientTaskExecutor");
            thread.setDaemon(true);
            return thread;
        });
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
