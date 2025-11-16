package io.github.jotabrc.ovy_mq_core.factories.interfaces;

import java.util.Map;

public interface AbstractFactory<R> {

    R create(Map<String, Object> definitions);
    Class<R> supports();
}