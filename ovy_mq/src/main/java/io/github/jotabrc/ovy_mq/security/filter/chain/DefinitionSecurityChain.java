package io.github.jotabrc.ovy_mq.security.filter.chain;

import io.github.jotabrc.ovy_mq.security.SecurityChainType;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.ClientType;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class DefinitionSecurityChain extends AbstractSecurityChain {

    private final ObjectProvider<DefinitionMap> definitionProvider;

    @Override
    public DefinitionMap handle(DefinitionMap definition) {
        HttpServletRequest req = definition.extract(Key.FILTER_SERVLET_REQUEST, HttpServletRequest.class);
        String auth = req.getHeader(Key.HEADER_AUTHORIZATION);
        String clientId = req.getHeader(Key.HEADER_CLIENT_ID);
        String topic = req.getHeader(Key.HEADER_TOPIC);
        String clientType = req.getHeader(Key.HEADER_CLIENT_TYPE);

        if (nonNull(topic) && !topic.isBlank()
                && nonNull(clientType) && !clientType.isBlank()
                && ((nonNull(clientId) && !clientId.isBlank()) || Objects.equals(ClientType.CONFIGURER.name(), clientType))) {

            definition = definitionProvider.getObject()
                    .add(Key.HEADER_AUTHORIZATION, auth)
                    .add(Key.HEADER_CLIENT_ID, clientId)
                    .add(Key.HEADER_TOPIC, topic)
                    .add(Key.HEADER_CLIENT_TYPE, clientType);
            return super.handleNext(definition);
        }

        throw new OvyException.AuthorizationDenied("Authorization denied");
    }

    @Override
    public SecurityChainType type() {
        return SecurityChainType.DEFINITION_CREATOR;
    }
}
