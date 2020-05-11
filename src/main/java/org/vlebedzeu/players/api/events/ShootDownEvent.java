package org.vlebedzeu.players.api.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Event of channel shooting down
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ShootDownEvent extends Event {
    /**
     * Parametrized constructor
     * @param senderId Sender Id
     */
    public ShootDownEvent(String senderId) {
        super(senderId);
    }

    @Override
    @JsonIgnore
    public EventType getType() {
        return EventType.SHOOT_DOWN;
    }
}
