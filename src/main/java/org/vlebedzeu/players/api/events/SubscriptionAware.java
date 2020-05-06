package org.vlebedzeu.players.api.events;

/**
 *
 */
public interface SubscriptionAware {
    void handleSubscribeEvent(SubscribeEvent event);
    void handleUnsubscribeEvent(UnsubscribeEvent event);
}
