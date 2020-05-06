package org.vlebedzeu.players.api.events;

import lombok.Getter;

/**
 *
 */
public class MessageEvent extends Event {
    @Getter
    private final String sourceMessageId;

    @Getter
    private final String text;

    public MessageEvent(String senderId, String text) {
        super(senderId);
        this.sourceMessageId = null;
        this.text = text;
    }

    public MessageEvent(String senderId, String sourceMessageId, String text) {
        super(senderId);
        this.sourceMessageId = sourceMessageId;
        this.text = text;
    }
}
