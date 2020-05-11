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
public class MessageEvent extends Event {

    private String sourceMessageId;
    private String text;

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

    @Override
    @JsonIgnore
    public EventType getType() {
        return EventType.MESSAGE;
    }
}
