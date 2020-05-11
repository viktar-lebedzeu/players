package org.vlebedzeu.players.api;

import org.vlebedzeu.players.api.events.MessageEvent;

/**
 * Message broadcast sender
 */
public interface MessageBroadcastSender {
    /**
     * Sends the given event to all subscribers
     * @param event Event to send
     */
    void broadcast(MessageEvent event);
}
