package io.github.jotabrc.ovy_mq_core.domain.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OvyAction implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private List<OvyCommand> commands;
    private Object payload;

    public <R> R getPayloadAs(Class<R> type, ObjectMapper objectMapper) {
        return nonNull(this.payload)
                ? objectMapper.convertValue(this.payload, type)
                : null;
    }

    public List<OvyCommand> getCommands() {
        return (nonNull(this.commands) && !this.commands.isEmpty())
                ? this.commands
                : Collections.emptyList();
    }
}
