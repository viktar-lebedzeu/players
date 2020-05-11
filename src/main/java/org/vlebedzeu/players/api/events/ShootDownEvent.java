package org.vlebedzeu.players.api.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;

/**
 *
 */
@NoArgsConstructor
public class ShootDownEvent extends Event {

    public ShootDownEvent(String senderId) {
        super(senderId);
    }

    @Override
    @JsonIgnore
    public EventType getType() {
        return EventType.SHOOT_DOWN;
    }
}
