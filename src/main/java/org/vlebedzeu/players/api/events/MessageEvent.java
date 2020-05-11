package org.vlebedzeu.players.api.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Text message event
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MessageEvent extends Event {
    /** Source message Id */
    protected String sourceMessageId;
    /** Message text */
    protected String text;

    /**
     * Parametrized constructor
     * @param senderId Sender Id
     * @param text Message text
     */
    public MessageEvent(String senderId, String text) {
        super(senderId);
        this.sourceMessageId = null;
        this.text = text;
    }

    /**
     * Parametrized constructor
     * @param senderId Sender Id
     * @param sourceMessageId Source message Id
     * @param text Message text
     */
    public MessageEvent(String senderId, String sourceMessageId, String text) {
        super(senderId);
        this.sourceMessageId = sourceMessageId;
        this.text = text;
    }

    @Override
    @JsonIgnore
    public EventType getType() {
        return EventType.MESSAGE;
    }
}
