package org.vlebedzeu.players.api.events;

/**
 * Interface of subscription events handling
 */
public interface SubscriptionAware {
    /**
     * Handles player subscription event
     * @param event Subscription event
     */
    void handleSubscribeEvent(SubscribeEvent event);

    /**
     * Handles player unsubscription event
     * @param event Unsubscription event
     */
    void handleUnsubscribeEvent(UnsubscribeEvent event);
}
