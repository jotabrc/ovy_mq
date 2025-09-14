package io.github.jotabrc.ovy_mq.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Component
@ConfigurationProperties(prefix = "ovymq.task.active")
public class TaskConfig {

    private boolean topic;
    private boolean consumer;

    public boolean useRegistry() {
        return topic || consumer;
    }

    public boolean useTopicRegistry() {
        return topic;
    }
}
