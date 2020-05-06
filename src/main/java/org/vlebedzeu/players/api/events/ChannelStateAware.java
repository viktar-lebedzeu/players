package org.vlebedzeu.players.api.events;

/**
 *
 */
public interface ChannelStateAware {
    void handleReadyEvent(ReadyEvent event);
    void handleShootDownEvent(ShootDownEvent event);
}
