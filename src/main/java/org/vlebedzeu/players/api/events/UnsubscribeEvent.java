package org.vlebedzeu.players.api.events;

/**
 *
 */
public class UnsubscribeEvent extends Event {

    public UnsubscribeEvent(String senderId) {
        super(senderId);
    }
}
