package io.github.jotabrc.ovy_mq_core.factories;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class FactoryDto<T, R> {

    public Class<T> type;
    public Class<R> returns;
}
