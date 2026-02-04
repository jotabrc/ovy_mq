package io.github.jotabrc.ovy_mq_core.domain.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.jotabrc.ovy_mq_core.components.mapper.PayloadDeserializer;
import io.github.jotabrc.ovy_mq_core.components.mapper.PayloadSerializer;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OvyAction implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<OvyCommand> commands;

    @JsonSerialize(using = PayloadSerializer.class)
    @JsonDeserialize(using = PayloadDeserializer.class)
    private Object payload;
    private String payloadType;

    public void setPayload(Object payload) {
        if (isNull(payload)) throw new IllegalArgumentException("Payload is null");
        this.payload = payload;
        this.payloadType = payload.getClass().getName();
    }

    public <R> R getPayloadAs(Class<R> type, ObjectMapper objectMapper) {
        return objectMapper.convertValue(this.payload, type);
    }

    public List<OvyCommand> getCommands() {
        return (nonNull(this.commands) && !this.commands.isEmpty())
                ? this.commands
                : Collections.emptyList();
    }

    private OvyAction(Builder builder) {
        this.commands = builder.commands;
        this.payload = builder.payload;
        if (nonNull(builder.payload)) {
            this.payloadType = builder.payload.getClass().getName();
        }
    }

    public static class Builder {
        private List<OvyCommand> commands;
        private Object payload;

        public Builder commands(List<OvyCommand> commands) {
            this.commands = commands;
            return this;
        }

        public Builder payload(Object payload) {
            this.payload = payload;
            return this;
        }

        public OvyAction build() {
            return new OvyAction(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
