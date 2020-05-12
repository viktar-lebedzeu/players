package org.vlebedzeu.players.api.socket;

import org.vlebedzeu.players.api.events.InOutEvent;
import org.vlebedzeu.players.api.events.SubscribeEvent;
import org.vlebedzeu.players.api.events.UnsubscribeEvent;

/**
 * Interface of socket event queue
 */
public interface SocketEventQueueAware {
    /**
     * Adds event into queue and unlocks it
     * @param event Event to add
     */
    void addEventAndUnlock(InOutEvent event);

    /**
     * Handles player subscription event
     * @param event Subscription event
     */
    void playerSubscribed(SubscribeEvent event);

    /**
     * Handles player unsubscription event
     * @param event Unsubscription event
     */
    void playerUnsubscribed(UnsubscribeEvent event);

    /**
     * Stops socket event queue
     */
    void stop();
}
