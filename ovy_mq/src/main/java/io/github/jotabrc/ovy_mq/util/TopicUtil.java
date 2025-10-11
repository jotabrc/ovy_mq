package io.github.jotabrc.ovy_mq.util;

import io.github.jotabrc.ovy_mq.domain.MessageStatus;

public class TopicUtil {

    private TopicUtil() {}

    public static String createTopicKeyForAwaitProcessing(String topic) {
        return topic + ":" + MessageStatus.AWAITING_PROCESSING;
    }

    public static String createTopicKeyForProcessing(String topic) {
        return topic + ":" + MessageStatus.PROCESSING;
    }

    public static String createTopicKey(String topic, MessageStatus messageStatus) {
        return topic + ":" + messageStatus;
    }
}
