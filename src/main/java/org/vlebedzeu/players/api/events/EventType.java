package org.vlebedzeu.players.api.events;

/**
 * Enumeration of event types
 */
public enum EventType {
    // Channel ready and shoot down
    READY, SHOOT_DOWN,
    // Player subscribed and unsubscribed
    SUBSCRIBE, UNSUBSCRIBE,
    // Broadcast and direct text message
    MESSAGE, DIRECT_MESSAGE
}
