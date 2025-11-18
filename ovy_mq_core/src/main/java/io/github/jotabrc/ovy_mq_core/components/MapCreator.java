package io.github.jotabrc.ovy_mq_core.components;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Component
public class MapCreator {

    public record MapCreatorDto(String key, Object value) {
    }

    public MapCreatorDto createDto(String key, Object value) {
        return new MapCreatorDto(key, value);
    }

    public Map<String, Object> create(MapCreatorDto... keyValues) {
        return Arrays.stream(keyValues)
                .collect(Collectors.toMap(MapCreatorDto::key,
                        MapCreatorDto::value,
                        (old, newValue) -> newValue));
    }

    public static <R> R extract(Map<String, Object> map, String key, Class<R> returningType) {
        Object value = map.remove(key);
        return nonNull(value)
                ? returningType.cast(value)
                : null;
    }

    public static <R> List<R> extractToList(Map<String, Object> map, String key, Class<R> returningType) {
        Object value = map.remove(key);
        if (value instanceof List<?> list) {
            return list.stream()
                    .map(returningType::cast)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public static <R> Map<String, R> convert(Map<String, Object> map, Class<R> returningType) {
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> returningType.cast(entry.getValue())));
    }
}
