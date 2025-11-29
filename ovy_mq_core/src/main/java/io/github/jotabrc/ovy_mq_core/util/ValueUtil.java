package io.github.jotabrc.ovy_mq_core.util;

import static java.util.Objects.nonNull;

public class ValueUtil {

    private ValueUtil() {}

    public static Long get(Long value, Long globalValue, Boolean useGlobalValue) {
        return nonNull(value) && !useGlobalValue
                ? value
                : globalValue;
    }

    public static Integer get(Integer value, Integer globalValue, Boolean useGlobalValue) {
        return nonNull(value) && !useGlobalValue
                ? value
                : globalValue;
    }

}
