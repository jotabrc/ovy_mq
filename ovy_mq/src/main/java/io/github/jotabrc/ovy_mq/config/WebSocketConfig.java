package io.github.jotabrc.ovy_mq.config;

import io.github.jotabrc.ovy_mq.security.AuthInterceptor;
import io.github.jotabrc.ovy_mq.security.CustomHandshakeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

import static io.github.jotabrc.ovy_mq_core.constants.Mapping.*;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthInterceptor authInterceptor;
    private final MappingJackson2MessageConverter mappingJackson2MessageConverter;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(WS_REGISTRY)
                .setHandshakeHandler(new CustomHandshakeHandler())
                .addInterceptors(authInterceptor)
                .setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(WS_QUEUE, WS_HEALTH, WS_CONFIG);
        registry.setApplicationDestinationPrefixes(WS_REQUEST);
        registry.setUserDestinationPrefix(WS_USER);
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        messageConverters.add(mappingJackson2MessageConverter);
        return true;
    }
}