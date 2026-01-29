package io.github.jotabrc.ovy_mq_core.components.util.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface DefinitionMap extends Serializable {

    DefinitionMap add(String key, Object value);
    Object get(String key);
    Map<String, Object> getDefinitions();
    <R> R extract(String key, Class<R> returningType);
    <R> List<R> extractToList(String key, Class<R> returningType);
    <R> Map<String, R> convert(Class<R> returningType);
    <R> R extractOrGet(String key, R orDefault);
}
