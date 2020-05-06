package org.vlebedzeu.players.api;

import org.vlebedzeu.players.api.events.MessageEvent;

/**
 *
 */
public interface MessageBroadcastSender {
    void broadcast(MessageEvent event);
}
