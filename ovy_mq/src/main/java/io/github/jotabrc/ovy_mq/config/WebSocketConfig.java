package io.github.jotabrc.ovy_mq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.jotabrc.ovy_mq.security.AuthInterceptor;
import io.github.jotabrc.ovy_mq.security.CustomHandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

import static io.github.jotabrc.ovy_mq.config.BrokerMapping.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebSocketConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(RECEIVER_REGISTRATION)
                .setHandshakeHandler(new CustomHandshakeHandler())
                .addInterceptors(authInterceptor)
                .setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(SENDER_MESSAGE_TO_CLIENT,
                SENDER_CONFIGURATION_TO_CLIENT);
        registry.setApplicationDestinationPrefixes(DEFAULT_PREFIX);
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(mapper);
        messageConverters.add(converter);
        return true;
    }
}
