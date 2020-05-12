package org.vlebedzeu.players.api.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Direct message text event
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DirectMessageEvent extends MessageEvent {
    /** Destination player Id (receiver id) */
    protected String destinationPlayerId;

    /**
     * Parametrized constructor
     * @param senderId Sender Id
     * @param text Message text
     * @param destinationPlayerId Destination player Id (receiver Id)
     */
    public DirectMessageEvent(String senderId, String text, String destinationPlayerId) {
        super(senderId, text);
        this.destinationPlayerId = destinationPlayerId;
    }

    /**
     * Parametrized constructor
     * @param senderId Sender Id
     * @param sourceMessageId Source message Id
     * @param text Message text
     * @param destinationPlayerId Destination player Id (receiver Id)
     */
    public DirectMessageEvent(String senderId, String sourceMessageId, String text, String destinationPlayerId) {
        super(senderId, sourceMessageId, text);
        this.destinationPlayerId = destinationPlayerId;
    }

    @Override
    @JsonIgnore
    public EventType getType() {
        return EventType.DIRECT_MESSAGE;
    }
}
