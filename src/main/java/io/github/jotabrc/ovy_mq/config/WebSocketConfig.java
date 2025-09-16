package io.github.jotabrc.ovy_mq.config;

import io.github.jotabrc.ovy_mq.security.AuthInterceptor;
import io.github.jotabrc.ovy_mq.security.CustomHandshakeHandler;
import io.github.jotabrc.ovy_mq.service.BrokerMapping;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebSocketConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(BrokerMapping.REGISTER.getRoute())
                .setHandshakeHandler(new CustomHandshakeHandler())
                .addInterceptors(authInterceptor)
                .setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(BrokerMapping.SEND_TO_CONSUMER.getRoute()); // stompClient.subscribe("/topic/requested_topic", msg -> {...});
        registry.setApplicationDestinationPrefixes(BrokerMapping.RECEIVE_FROM_CONSUMER.getRoute()); // stompClient.send("/queue/controller_mapping", {}, "Mensagem para o servidor");
        registry.setUserDestinationPrefix("/user");
    }
}
