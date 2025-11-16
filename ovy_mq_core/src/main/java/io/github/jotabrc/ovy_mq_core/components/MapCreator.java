package io.github.jotabrc.ovy_mq_core.components;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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
}
