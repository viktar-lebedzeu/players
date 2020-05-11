package org.vlebedzeu.players.api.socket;

/**
 *
 */
public interface SocketChannelHandler {
    void onSocketChannelOpen(SocketHolder channelHolder);
    void onSocketChannelClosed(SocketHolder channelHolder);
    void onMessage(SocketHolder channelHolder, String message);
}
