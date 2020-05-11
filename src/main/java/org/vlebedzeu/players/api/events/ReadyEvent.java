package org.vlebedzeu.players.api.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Event of channel readiness
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReadyEvent extends Event {
    /**
     * Parametrized constructor
     * @param senderId Sender Id
     */
    public ReadyEvent(String senderId) {
        super(senderId);
    }

    @Override
    @JsonIgnore
    public EventType getType() {
        return EventType.READY;
    }
}
