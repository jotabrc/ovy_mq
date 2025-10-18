package io.github.jotabrc.ovy_mq_client.service.handler;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.domain.factory.ClientFactory;
import io.github.jotabrc.ovy_mq_client.service.OvyListener;
import io.github.jotabrc.ovy_mq_client.service.registry.interfaces.ClientRegistry;
import io.github.jotabrc.ovy_mq_client.service.handler.interfaces.ClientSessionHandler;
import io.github.jotabrc.ovy_mq_client.service.ApplicationContextHolder;
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
public class ClientListenerInitializer implements CommandLineRunner {

    private final ClientSessionHandler clientSessionHandler;
    private final ClientRegistry clientRegistry;

    @Override
    public void run(String... args) {
        initialize();
    }

    public void initialize() {
        log.info("Listener handler initialized");
        String[] beanNames = ApplicationContextHolder.get().getBeanDefinitionNames();
        log.info("Searching for clients");
        for (String beanName : beanNames) {
            Object bean = ApplicationContextHolder.get().getBean(beanName);
            Class<?> beanClass = bean.getClass();

            for (Method method : beanClass.getMethods()) {
                OvyListener listener = AnnotationUtils.findAnnotation(method, OvyListener.class);

                if (nonNull(listener)) {
                    log.info("Found listener for topic={} in class={} on method={}", listener.topic(), beanClass.getSimpleName(), method.getName());
                    String topic = listener.topic();
                    log.info("Listener for topic={} has {} replica(s)", topic, listener.replicas());
                    for (int i = 0; i < listener.replicas(); i++) {
                        Client client = ClientFactory.of(topic, method);
                        log.info("Creating client {}/{} for topic={}", i + 1, listener.replicas(), listener.topic());
                        clientSessionHandler.initialize(client);
                        clientRegistry.save(client);
                    }
                }
            }
        }
    }
}
