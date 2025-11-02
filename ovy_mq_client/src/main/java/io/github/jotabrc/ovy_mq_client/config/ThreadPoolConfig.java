package io.github.jotabrc.ovy_mq_client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ThreadPoolConfig implements AsyncConfigurer {

    public static final String DEFAULT_EXECUTOR = "defaultExecutor";
    public static final String LISTENER_EXECUTOR = "listenerExecutor";

    @Override
    @Bean(name = DEFAULT_EXECUTOR)
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(3);
        executor.setThreadNamePrefix("Default-ThreadPool-");
        executor.initialize();
        return executor;
    }

    @Bean(name = LISTENER_EXECUTOR)
    public Executor listenerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("MessagePayloadHandler-ThreadPool-");
        executor.initialize();
        return executor;
    }
}
