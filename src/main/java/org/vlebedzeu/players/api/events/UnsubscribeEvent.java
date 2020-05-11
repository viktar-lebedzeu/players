package org.vlebedzeu.players.api.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Event of player unsubscription
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UnsubscribeEvent extends Event {
    /**
     * Parametrized constructor
     * @param senderId Sender Id
     */
    public UnsubscribeEvent(String senderId) {
        super(senderId);
    }

    @Override
    @JsonIgnore
    public EventType getType() {
        return EventType.UNSUBSCRIBE;
    }
}
