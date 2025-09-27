package io.github.jotabrc.ovy_mq_client.service.task;

import io.github.jotabrc.ovy_mq_client.service.ConsumerListener;
import io.github.jotabrc.ovy_mq_client.service.ServerSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Component
@ConditionalOnProperty(
        name = "ovymq.task.consumer.active",
        havingValue = "true",
        matchIfMissing = false
)
public class ConsumerTask implements ApplicationContextAware {

    private ApplicationContext context;
    private ServerSession serverSession;

    @Scheduled(fixedDelayString = "${ovymq.task.consumer.delay}")
    public void execute() {
        Map<String, Object> beans = context.getBeansWithAnnotation(ConsumerListener.class);
        beans.forEach((k, v) -> Arrays.stream(v.getClass().getDeclaredMethods()).forEach(method -> {
            ConsumerListener annotation = method.getAnnotation(ConsumerListener.class);
            String topic = annotation.value();
            serverSession.requestMessage(topic);
        }));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
