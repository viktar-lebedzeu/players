package org.vlebedzeu.players.api.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Base event
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Event {
    /** Unique event Id (randomly generated) */
    protected String eventId = RandomStringUtils.randomAlphanumeric(30);
    /** Sender Id */
    protected String senderId;

    /**
     * Parametrized constructor
     * @param senderId Sender Id
     */
    public Event(String senderId) {
        this.senderId = senderId;
    }

    /**
     * Returns event type of current event instance
     * @return Event type
     */
    @JsonIgnore
    public EventType getType() {
        return null;
    }
}
