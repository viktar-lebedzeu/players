package org.vlebedzeu.players.api;

import org.vlebedzeu.players.api.events.DirectMessageEvent;
import org.vlebedzeu.players.api.events.MessageEvent;

/**
 *
 */
public interface MessageSender {
    void send(DirectMessageEvent event);
}
