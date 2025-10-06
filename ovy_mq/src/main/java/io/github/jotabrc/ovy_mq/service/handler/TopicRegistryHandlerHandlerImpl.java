package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.service.handler.interfaces.TopicRegistryHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;

@Getter
@AllArgsConstructor
@Component
public class TopicRegistryHandlerHandlerImpl implements TopicRegistryHandler {

    private Set<String> topics;

    @Override
    public void save(String topic) {
        if (isNull(this.topics)) {
            this.topics = new HashSet<>();
        }
        topics.add(topic);
    }

    @Override
    public Set<String> getTopicList() {
        return new HashSet<>(this.topics);
    }
}
