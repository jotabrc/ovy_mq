package io.github.jotabrc.ovy_mq_client.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    public static <T> T getBean(Class<T> beanClassName) {
        return applicationContext.getBean(beanClassName);
    }

    public static ApplicationContext get() {
        return applicationContext;
    }
}
