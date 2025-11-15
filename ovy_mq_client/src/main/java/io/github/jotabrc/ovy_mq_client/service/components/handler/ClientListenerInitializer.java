package io.github.jotabrc.ovy_mq_client.service.components.handler;

import io.github.jotabrc.ovy_mq_client.service.OvyListener;
import io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces.SessionInitializer;
import io.github.jotabrc.ovy_mq_client.service.registry.ClientRegistry;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.ClientType;
import io.github.jotabrc.ovy_mq_core.factories.ClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientListenerInitializer implements BeanPostProcessor {

    private final SessionInitializer sessionInitializer;
    private final ClientRegistry clientRegistry;

    private final Executor smallPoolExecutor;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        smallPoolExecutor.execute(() -> {
            log.info("Bean post-processing: bean={}", beanName);
            Class<?> beanClass = bean.getClass();

            for (Method method : beanClass.getMethods()) {
                OvyListener listener = AnnotationUtils.findAnnotation(method, OvyListener.class);

                if (nonNull(listener)) {
                    log.info("Listener: topic={} replicas={}", listener.topic(), listener.replicas());
                    for (int i = 0; i < listener.replicas(); i++) {
                        Client client = ClientFactory.of(listener.topic(), method, beanName, listener.timeout(), ClientType.CONSUMER);
                        log.info("Creating client: replica={}/{} topic={} config=[maxReplicas={} minReplicas={} stepReplicas={} autoManageReplicas={} timeout={}ms]",
                                i + 1,
                                listener.replicas(),
                                listener.topic(),
                                listener.maxReplicas(),
                                listener.minReplicas(),
                                listener.stepReplicas(),
                                listener.autoManageReplicas(),
                                listener.timeout());
                        sessionInitializer.createSessionAndConnect(client);
                        clientRegistry.save(client);
                    }
                }
            }
        });
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
