package io.github.jotabrc.ovy_mq_core.components;

import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DefinitionMapImpl implements DefinitionMap {

    @Serial
    private static final long serialVersionUID = 1L;

    private Map<String, Object> definitions = new HashMap<>();

    @Override
    public DefinitionMap add(String key, Object value) {
        if (nonNull(key) && nonNull(value)) {
            this.definitions.put(key, value);
        }
        return this;
    }

    @Override
    public Object get(String key) {
        return definitions.get(key);
    }

    @Override
    public Map<String, Object> getDefinitions() {
        return new HashMap<>(this.definitions);
    }

    @Override
    public <R> R extract(String key, Class<R> returningType) {
        Object value = this.getDefinitions().remove(key);
        return nonNull(value)
                ? returningType.cast(value)
                : null;
    }

    @Override
    public <R> List<R> extractToList(String key, Class<R> returningType) {
        Object value = this.getDefinitions().remove(key);
        if (value instanceof List<?> list) {
            return list.stream()
                    .map(returningType::cast)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public <R> Map<String, R> convert(Class<R> returningType) {
        return this.getDefinitions().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> returningType.cast(entry.getValue())));
    }

    @Override
    public  <R> R extractOrGet(String key, R get) {
        var result = this.extract(key, get.getClass());
        return isNull(result)
                ? get
                : (R) result;
    }
}