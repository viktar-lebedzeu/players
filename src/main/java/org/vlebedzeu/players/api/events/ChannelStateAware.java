package org.vlebedzeu.players.api.events;

/**
 * Interface of channel state handlers
 */
public interface ChannelStateAware {
    /**
     * Handles event of channel readiness
     * @param event Ready event
     */
    void handleReadyEvent(ReadyEvent event);

    /**
     * Handles event of channel shoot down
     * @param event Shoot down event
     */
    void handleShootDownEvent(ShootDownEvent event);
}
