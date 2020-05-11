package org.vlebedzeu.players.api.events;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 */
@Data
@AllArgsConstructor
public class InOutEvent {
    public enum InOutType {
        IN, OUT
    }

    private InOutType type;
    private Event event;
}
