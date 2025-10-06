package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.config.ServerHandlerAction;

public enum ServerCommand {

    EXECUTE{
        @Override
        public void execute(ServerHandlerAction serverHandlerAction) {

        }
    };

    public abstract void execute(ServerHandlerAction serverHandlerAction);
}
