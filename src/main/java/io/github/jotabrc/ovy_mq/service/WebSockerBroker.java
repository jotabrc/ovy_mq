package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.security.AuthInterceptor;
import io.github.jotabrc.ovy_mq.security.CustomHandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSockerBroker implements WebSocketMessageBrokerConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebSockerBroker(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/register")
                .setHandshakeHandler(new CustomHandshakeHandler())
                .addInterceptors(authInterceptor)
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/send"); // cliente escute stompClient.subscribe("/send/chat", msg -> {...});
        registry.setApplicationDestinationPrefixes("/receive"); // stompClient.send("/receive/message", {}, "Mensagem para o servidor");
        registry.setUserDestinationPrefix("/user");
    }
}
