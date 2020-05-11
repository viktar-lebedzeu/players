package org.vlebedzeu.players.api.socket;

import org.vlebedzeu.players.api.events.InOutEvent;
import org.vlebedzeu.players.api.events.SubscribeEvent;
import org.vlebedzeu.players.api.events.UnsubscribeEvent;

/**
 *
 */
public interface SocketEventQueueAware {
    void addEventAndUnlock(InOutEvent event);
    void playerSubscribed(SubscribeEvent event);
    void playerUnsubscribed(UnsubscribeEvent event);
    void stop();
}
