package io.github.jotabrc.ovy_mq.security.filter.chain;

import io.github.jotabrc.ovy_mq.registry.ConfigClientContextHolder;
import io.github.jotabrc.ovy_mq_core.chain.ChainType;
import io.github.jotabrc.ovy_mq_core.chain.AbstractChain;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class SubjectChain extends AbstractChain {

    private final ConfigClientContextHolder configClientContextHolder;

    @Override
    public DefinitionMap handle(DefinitionMap definition) {
        String subject = definition.extract(OvyMqConstants.CLIENT_ID, String.class);
        String clientType = definition.extract(OvyMqConstants.CLIENT_TYPE, String.class);

        if ((isNull(subject) || subject.isBlank())
                && nonNull(clientType) && Objects.equals(ClientType.CONFIGURER.name(), clientType)) {
            subject = configClientContextHolder.getId()
                    .orElseThrow(() -> new OvyException.SecurityFilterFailure("Config client not available"));
        }

        if (nonNull(subject) && !subject.isBlank()) {
            definition.add(OvyMqConstants.FILTER_SUBJECT, subject);
            return handleNext(definition);
        }

        throw new OvyException.AuthorizationDenied("Authorization denied");
    }

    @Override
    public ChainType type() {
        return ChainType.SUBJECT_IDENTIFIER;
    }
}
