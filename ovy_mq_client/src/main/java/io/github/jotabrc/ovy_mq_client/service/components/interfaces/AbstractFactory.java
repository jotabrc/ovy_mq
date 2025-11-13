package io.github.jotabrc.ovy_mq_client.service.components.interfaces;

import java.util.Map;

public interface AbstractFactory<R, T> {

    R create(Map<String, T> definitions);
    Class<R> supports();
}
