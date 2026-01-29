package io.github.jotabrc.ovy_mq_core.domain.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.lang.reflect.Method;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientExecution {

    @JsonIgnore
    private transient String beanName;
    @JsonIgnore
    private transient Method method;
}
