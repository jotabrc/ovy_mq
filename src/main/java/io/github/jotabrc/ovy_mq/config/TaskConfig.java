package io.github.jotabrc.ovy_mq.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Component
@ConfigurationProperties(prefix = "ovymq.task")
public class TaskConfig {

    private boolean topicActive;
    private boolean consumerActive;
    private boolean reprocessingActive;

    @Getter
    private long reprocessingDelay;

    public boolean useRegistry() {
        return topicActive || consumerActive;
    }

    public boolean useTopicRegistry() {
        return topicActive;
    }
}
