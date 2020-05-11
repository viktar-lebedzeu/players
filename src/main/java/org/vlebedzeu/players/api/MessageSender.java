package org.vlebedzeu.players.api;

import org.vlebedzeu.players.api.events.DirectMessageEvent;

/**
 * Interface of message sender
 */
public interface MessageSender {
    /**
     * Sends direct text message
     * @param event Direct text message event
     */
    void send(DirectMessageEvent event);
}
