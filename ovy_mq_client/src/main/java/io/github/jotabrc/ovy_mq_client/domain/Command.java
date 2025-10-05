package io.github.jotabrc.ovy_mq_client.domain;

import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientMessageHandler;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientRegistryHandler;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientSessionInitializerHandler;

import static io.github.jotabrc.ovy_mq_client.service.domain.client.handler.ClientHandler.*;

public enum Command {

    INITIALIZE_SESSION {
        @Override
        public void execute(Action action) {
            ((ClientSessionInitializerHandler) SESSION_INITIALIZER.getHandler()).initializeSession(action.getClient());
        }
    },

    SEND_MESSAGE_TO_CONSUMER {
        @Override
        public void execute(Action action) {
            ((ClientRegistryHandler) CLIENT_HANDLER.getHandler()).executeListener(action.getClient().getTopic(), action.getMessagePayload());
        }
    },

    SAVE_CLIENT_IN_REGISTRY {
        @Override
        public void execute(Action action) {
            ((ClientRegistryHandler) CLIENT_HANDLER.getHandler()).save(action.getClient());
        }
    },

    PROCESS_RECEIVED_MESSAGE {
        @Override
        public void execute(Action action) {
            ((ClientMessageHandler) CLIENT_MESSAGE.getHandler()).handleMessage(action.getClient().getTopic(), action.getMessagePayload());
        }
    },

    REQUEST_MESSAGES_FOR_ALL_AVAILABLE_CLIENTS {
        @Override
        public void execute(Action action) {
            ((ClientRegistryHandler) CLIENT_HANDLER.getHandler()).requestMessagesForAllAvailableClients();
        }
    };

    public abstract void execute(Action action);
}
