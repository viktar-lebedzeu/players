package org.vlebedzeu.players.api;

import org.vlebedzeu.players.api.players.Player;

/**
 * Communication channel interface
 */
public interface Channel extends MessageSender, MessageBroadcastSender {
    void subscribePlayer(Player player);
    void unsubscribePlayer(Player player);

    void start() throws InitializationException;
    void stop();
}
