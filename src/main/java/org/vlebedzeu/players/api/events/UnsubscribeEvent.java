package org.vlebedzeu.players.api.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;

/**
 *
 */
@NoArgsConstructor
public class UnsubscribeEvent extends Event {

    public UnsubscribeEvent(String senderId) {
        super(senderId);
    }

    @Override
    @JsonIgnore
    public EventType getType() {
        return EventType.UNSUBSCRIBE;
    }
}
