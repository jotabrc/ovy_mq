package io.github.jotabrc.ovy_mq.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "ovymq.task")
public class TaskConfig {

    private Consumer consumer;
    private Topic topic;
    private Reprocessing reprocessing;

    @Setter @Getter
    public static class Consumer {
        private boolean active;
        private long delay;
    }

    @Setter
    @Getter
    public static class Topic {
        private boolean active;
        private long delay;
    }

    @Setter @Getter
    public static class Reprocessing {
        private boolean active;
        private long delay;
    }

    public boolean useRegistry() {
        return consumer.isActive() || topic.isActive();
    }

    public boolean useTopicRegistry() {
        return topic.isActive();
    }
}
