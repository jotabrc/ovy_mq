package io.github.jotabrc.ovy_mq_core.domain.action;

import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
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
    public DefinitionMap definitionMap;

    public List<OvyCommand> getCommands() {
        return (nonNull(this.commands) && !this.commands.isEmpty())
                ? this.getCommands()
                : Collections.emptyList();
    }
}
