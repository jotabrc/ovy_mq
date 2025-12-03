package io.github.jotabrc.ovy_mq_core.util;

import io.github.jotabrc.ovy_mq_core.domain.payload.MessageStatus;

public class TopicUtil {

    private TopicUtil() {}

    public static String createTopicKeyForAwaitProcessing(String topic) {
        return topic + ":" + MessageStatus.AWAITING_PROCESSING;
    }

    public static String createTopicKeyForSent(String topic) {
        return topic + ":" + MessageStatus.SENT;
    }

    public static String createTopicKey(String topic, MessageStatus messageStatus) {
        return topic + ":" + messageStatus;
    }
}
