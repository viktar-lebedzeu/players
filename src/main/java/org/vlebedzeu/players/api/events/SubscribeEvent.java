package org.vlebedzeu.players.api.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Event of player subscription
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubscribeEvent extends Event {
    /**
     * Parametrized constructor
     * @param senderId Sender Id
     */
    public SubscribeEvent(String senderId) {
        super(senderId);
    }

    @Override
    @JsonIgnore
    public EventType getType() {
        return EventType.SUBSCRIBE;
    }
}
