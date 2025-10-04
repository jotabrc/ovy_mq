package io.github.jotabrc.ovy_mq_client.service.domain.client;

public class SessionFactory {

    public static ClientSession create() {
        return new ClientSession();
    }
}
