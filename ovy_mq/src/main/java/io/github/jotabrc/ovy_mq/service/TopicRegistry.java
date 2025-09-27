package io.github.jotabrc.ovy_mq.service;

import java.util.Set;

public interface TopicRegistry {

    void save(String topic);
    Set<String> getTopicList();
}
