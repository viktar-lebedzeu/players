package org.vlebedzeu.players.api;

import org.vlebedzeu.players.api.players.Player;

/**
 * Communication channel interface
 */
public interface Channel extends MessageSender, MessageBroadcastSender {
    /**
     * Subscribes player
     * @param player Player to subscribe
     */
    void subscribePlayer(Player player);

    /**
     * Unsubscribes player
     * @param player Player to unsubscribe
     */
    void unsubscribePlayer(Player player);

    /**
     * Starts channel
     * @throws InitializationException In case of any initialization error
     */
    void start() throws InitializationException;

    /**
     * Stops channel
     */
    void stop();
}
