package org.vlebedzeu.players.api.events;

import lombok.Getter;

/**
 *
 */
public class DirectMessageEvent extends MessageEvent {
    @Getter
    private final String destinationPlayerId;

    public DirectMessageEvent(String senderId, String text, String destinationPlayerId) {
        super(senderId, text);
        this.destinationPlayerId = destinationPlayerId;
    }

    public DirectMessageEvent(String senderId, String sourceMessageId, String text, String destinationPlayerId) {
        super(senderId, sourceMessageId, text);
        this.destinationPlayerId = destinationPlayerId;
    }
}
