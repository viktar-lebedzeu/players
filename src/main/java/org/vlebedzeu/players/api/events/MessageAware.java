package org.vlebedzeu.players.api.events;

/**
 * Interface of handling text message
 */
public interface MessageAware {
    /**
     * Handles text message event
     * @param event Text message event
     */
    void handleMessageEvent(MessageEvent event);
}
