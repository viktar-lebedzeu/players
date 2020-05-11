package org.vlebedzeu.players.api.socket;

import org.vlebedzeu.players.api.events.Event;

import java.io.IOException;

/**
 *
 */
public interface SocketConnection {
    SocketConnection start() throws IOException;
    void stop();
    void stopNow();

    void sendEvent(Event event);
    boolean isServerMode();
}
