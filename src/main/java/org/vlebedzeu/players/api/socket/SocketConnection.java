package org.vlebedzeu.players.api.socket;

import org.vlebedzeu.players.api.events.Event;

import java.io.IOException;

/**
 * Socket connection interface
 */
public interface SocketConnection {
    /**
     * Starts socket connection
     * @return Socket connection instance
     * @throws IOException If connection fails
     */
    SocketConnection start() throws IOException;

    /**
     * Stops socket connection
     */
    void stop();

    /**
     * Stops socket connection immediately
     */
    void stopNow();

    /**
     * Sends the given event
     * @param event Event to send
     */
    void sendEvent(Event event);

    /**
     * Returns if connection works in server mode
     * @return True if connection works in server mode, false otherwise
     */
    boolean isServerMode();
}
