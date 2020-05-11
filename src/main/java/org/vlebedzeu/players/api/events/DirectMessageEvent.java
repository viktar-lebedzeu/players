package org.vlebedzeu.players.api.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DirectMessageEvent extends MessageEvent {
    private String destinationPlayerId;

    public DirectMessageEvent(String senderId, String text, String destinationPlayerId) {
        super(senderId, text);
        this.destinationPlayerId = destinationPlayerId;
    }

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
