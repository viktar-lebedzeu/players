package org.vlebedzeu.players.api.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 *
 */
@Data
@NoArgsConstructor
public class Event {
    private String eventId = RandomStringUtils.randomAlphanumeric(30);
    private String senderId;

    public Event(String senderId) {
        this.senderId = senderId;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @JsonIgnore
    public EventType getType() {
        return null;
    }
}
