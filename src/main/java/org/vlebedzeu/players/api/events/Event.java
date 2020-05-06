package org.vlebedzeu.players.api.events;

import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 */
public class Event {
    @Getter
    private final String senderId;

    @Getter
    private final String eventId;

    protected Event(String senderId) {
        this.senderId = senderId;
        this.eventId = RandomStringUtils.randomAlphanumeric(50);
    }
}
