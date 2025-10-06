package io.github.jotabrc.ovy_mq.service.handler.interfaces;

import java.util.Set;

public interface TopicRegistryHandler extends AbstractHandler {

    void save(String topic);
    Set<String> getTopicList();
}
