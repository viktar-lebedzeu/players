package org.vlebedzeu.players.api.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;

/**
 *
 */
@NoArgsConstructor
public class SubscribeEvent extends Event {

    public SubscribeEvent(String senderId) {
        super(senderId);
    }

    @Override
    @JsonIgnore
    public EventType getType() {
        return EventType.SUBSCRIBE;
    }
}
