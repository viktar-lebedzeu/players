package org.vlebedzeu.players.api;

/**
 * Message source interface
 */
public interface MessageSource {
    /**
     * Returns nex message to send it by primary player (aka "initiator")
     * @return Text message
     */
    String nextMessage();
}
