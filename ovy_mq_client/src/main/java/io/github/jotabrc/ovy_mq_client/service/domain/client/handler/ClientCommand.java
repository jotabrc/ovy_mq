package io.github.jotabrc.ovy_mq_client.service.domain.client.handler;

import io.github.jotabrc.ovy_mq_client.domain.ClientHandlerAction;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientMessageHandler;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientRegistryHandler;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientSessionInitializerHandler;

import static io.github.jotabrc.ovy_mq_client.service.domain.client.handler.ClientHandler.*;

public enum ClientCommand {

    INITIALIZE_SESSION {
        @Override
        public void execute(ClientHandlerAction clientHandlerAction) {
            ((ClientSessionInitializerHandler) SESSION_INITIALIZER.getHandler()).initializeSession(clientHandlerAction.getClient());
        }
    },

    SEND_MESSAGE_TO_CONSUMER {
        @Override
        public void execute(ClientHandlerAction clientHandlerAction) {
            ((ClientRegistryHandler) CLIENT_HANDLER.getHandler()).executeListener(clientHandlerAction.getClient().getTopic(), clientHandlerAction.getMessagePayload());
        }
    },

    SAVE_CLIENT_IN_REGISTRY {
        @Override
        public void execute(ClientHandlerAction clientHandlerAction) {
            ((ClientRegistryHandler) CLIENT_HANDLER.getHandler()).save(clientHandlerAction.getClient());
        }
    },

    PROCESS_RECEIVED_MESSAGE {
        @Override
        public void execute(ClientHandlerAction clientHandlerAction) {
            ((ClientMessageHandler) CLIENT_MESSAGE.getHandler()).handleMessage(clientHandlerAction.getClient().getTopic(), clientHandlerAction.getMessagePayload());
        }
    },

    REQUEST_MESSAGES_FOR_ALL_AVAILABLE_CLIENTS {
        @Override
        public void execute(ClientHandlerAction clientHandlerAction) {
            ((ClientRegistryHandler) CLIENT_HANDLER.getHandler()).requestMessagesForAllAvailableClients();
        }
    };

    public abstract void execute(ClientHandlerAction clientHandlerAction);
}
