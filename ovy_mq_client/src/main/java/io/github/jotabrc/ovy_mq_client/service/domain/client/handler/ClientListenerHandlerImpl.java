package io.github.jotabrc.ovy_mq_client.service.domain.client.handler;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.domain.factory.ClientFactory;
import io.github.jotabrc.ovy_mq_client.service.domain.client.OvyListener;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientListenerHandler;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientRegistryHandler;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientSessionInitializerHandler;
import io.github.jotabrc.ovy_mq_client.util.ApplicationContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientListenerHandlerImpl implements ClientListenerHandler, CommandLineRunner {

    private final ClientSessionInitializerHandler clientSessionInitializerHandler;
    private final ClientRegistryHandler clientRegistryHandler;

    @Override
    public void run(String... args) throws Exception {
        initialize();
    }

    @Override
    public void initialize() {
        log.info("Initializing session handler...");
        String[] beanNames = ApplicationContextHolder.get().getBeanDefinitionNames();
        log.info("Looking up methods for listeners...");
        for (String beanName : beanNames) {
            Object bean = ApplicationContextHolder.get().getBean(beanName);
            Class<?> beanClass = bean.getClass();

            for (Method method : beanClass.getMethods()) {
                OvyListener listener = AnnotationUtils.findAnnotation(method, OvyListener.class);
                if (nonNull(listener)) {
                    log.info("Found listener for topic {} in class {} on method {}", listener.topic(), beanClass.getSimpleName(), method.getName());
                    String topic = listener.topic();

                    Client client = ClientFactory.createConsumer(topic, method);
                    clientSessionInitializerHandler.initializeSession(client);
                    clientRegistryHandler.save(client);
                }
            }
        }
        log.info("Session initialization completed");
    }
}
