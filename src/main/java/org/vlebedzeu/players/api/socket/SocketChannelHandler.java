package org.vlebedzeu.players.api.socket;

/**
 * Socket channel handler interface
 */
public interface SocketChannelHandler {
    /**
     * Calls when new socked channel is opened
     * @param channelHolder Socket holder
     */
    void onSocketChannelOpen(SocketHolder channelHolder);

    /**
     * Calls when socket channel is closed
     * @param channelHolder Socket holder
     */
    void onSocketChannelClosed(SocketHolder channelHolder);

    /**
     * Calls when socked channel is received message
     * @param channelHolder socket holder
     * @param message Message string
     */
    void onMessage(SocketHolder channelHolder, String message);
}
