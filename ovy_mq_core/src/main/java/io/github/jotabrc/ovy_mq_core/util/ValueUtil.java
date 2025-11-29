package io.github.jotabrc.ovy_mq_core.util;

import static java.util.Objects.nonNull;

public class ValueUtil {

    private ValueUtil() {}

    public static Long get(Long value, Long defaultValue, Boolean useGlobalValues) {
        return nonNull(value) && !useGlobalValues
                ? value
                : defaultValue;
    }

    public static Integer get(Integer value, Integer defaultValue, Boolean useGlobalValues) {
        return nonNull(value) && !useGlobalValues
                ? value
                : defaultValue;
    }

}
