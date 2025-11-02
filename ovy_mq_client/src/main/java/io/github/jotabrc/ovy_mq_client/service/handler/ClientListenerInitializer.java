package io.github.jotabrc.ovy_mq_client.service.handler;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.domain.ListenerState;
import io.github.jotabrc.ovy_mq_client.domain.factory.ClientFactory;
import io.github.jotabrc.ovy_mq_client.service.ApplicationContextHolder;
import io.github.jotabrc.ovy_mq_client.service.OvyListener;
import io.github.jotabrc.ovy_mq_client.service.handler.interfaces.ClientSessionInitializerHandler;
import io.github.jotabrc.ovy_mq_client.service.registry.interfaces.ClientRegistry;
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

    private final ClientSessionInitializerHandler clientSessionInitializerHandler;
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
            Object bean = ApplicationContextHolder.getContextBean(beanName);
            Class<?> beanClass = bean.getClass();

            for (Method method : beanClass.getMethods()) {
                OvyListener listener = AnnotationUtils.findAnnotation(method, OvyListener.class);

                if (nonNull(listener)) {
                    ListenerState listenerState = createListenerState(listener);
                    log.info("Listener: topic={} class={} method={} replicas={}", listener.topic(), beanClass.getSimpleName(), method.getName(), listener.replicas());
                    for (int i = 0; i < listener.replicas(); i++) {
                        Client client = ClientFactory.of(listener.topic(), method, beanName, listenerState);
                        log.info("Creating client: replica={}/{} topic={} class={} method={} config=[maxReplicas={} minReplicas={} stepReplicas={} autoManageReplicas={} timeout={}ms]",
                                i + 1,
                                listener.replicas(),
                                listener.topic(),
                                beanClass.getSimpleName(),
                                method.getName(),
                                listener.maxReplicas(),
                                listener.minReplicas(),
                                listener.stepReplicas(),
                                listener.autoManageReplicas(),
                                listener.timeout());
                        clientSessionInitializerHandler.initialize(client);
                        clientRegistry.save(client);
                    }
                }
            }
        }
    }

    private static ListenerState createListenerState(OvyListener listener) {
        return ListenerState.builder()
                .topic(listener.topic())
                .replicas(listener.replicas())
                .maxReplicas(listener.maxReplicas())
                .minReplicas(listener.minReplicas())
                .stepReplicas(listener.stepReplicas())
                .autoManageReplicas(listener.autoManageReplicas())
                .timeout(listener.timeout())
                .build();
    }
}
