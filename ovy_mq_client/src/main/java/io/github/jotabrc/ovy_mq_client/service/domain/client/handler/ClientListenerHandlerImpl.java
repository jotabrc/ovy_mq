package io.github.jotabrc.ovy_mq_client.service.domain.client.handler;

import io.github.jotabrc.ovy_mq_client.domain.*;
import io.github.jotabrc.ovy_mq_client.service.domain.client.OvyListener;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientListenerHandler;
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

    @Override
    public void execute() {
        initializeSessionHandler();
    }

    @Override
    public void run(String... args) throws Exception {
        initializeSessionHandler();
    }

    @Override
    public void initializeSessionHandler() {
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

                    Client client = ClientFactory.createConsumer(topic, null, method);
                    Action action = ActionFactory.create(client, null, Command.EXECUTE_CLIENT_SESSION_INITIALIZER);
                    ClientHandler.CLIENT_INITIALIZE_SESSION.getHandler().execute(action);

                    action.setCommand(Command.EXECUTE_CLIENT_METHOD_HANDLER_PUT_IF_ABSENT);
                    ClientHandler.CLIENT_METHOD.getHandler().execute(action);
                }
            }
        }
        log.info("Session initialization completed");
    }
}
