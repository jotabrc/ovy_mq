package io.github.jotabrc.ovy_mq.security.filter.chain;

import io.github.jotabrc.ovy_mq_core.chain.ChainType;
import io.github.jotabrc.ovy_mq_core.chain.AbstractChain;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
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
public class DefinitionChain extends AbstractChain {

    private final ObjectProvider<DefinitionMap> definitionProvider;

    @Override
    public DefinitionMap handle(DefinitionMap definition) {
        HttpServletRequest req = definition.extract(OvyMqConstants.FILTER_SERVLET_REQUEST, HttpServletRequest.class);
        String auth = req.getHeader(OvyMqConstants.AUTHORIZATION);
        String clientId = req.getHeader(OvyMqConstants.CLIENT_ID);
        String topic = req.getHeader(OvyMqConstants.SUBSCRIBED_TOPIC);
        String clientType = req.getHeader(OvyMqConstants.CLIENT_TYPE);
        String role = req.getHeader(OvyMqConstants.ROLES);

        if (nonNull(topic) && !topic.isBlank()
                && nonNull(clientType) && !clientType.isBlank()
                && ((nonNull(clientId) && !clientId.isBlank()) || Objects.equals(ClientType.CONFIGURER.name(), clientType))) {

            definition = definitionProvider.getObject()
                    .add(OvyMqConstants.AUTHORIZATION, auth)
                    .add(OvyMqConstants.CLIENT_ID, clientId)
                    .add(OvyMqConstants.SUBSCRIBED_TOPIC, topic)
                    .add(OvyMqConstants.CLIENT_TYPE, clientType)
                    .add(OvyMqConstants.ROLES, role);
            return super.handleNext(definition);
        }

        throw new OvyException.AuthorizationDenied("Authorization denied");
    }

    @Override
    public ChainType type() {
        return ChainType.DEFINITION_CREATOR;
    }
}
