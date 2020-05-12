package org.vlebedzeu.players.api.events;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Event wrapper that helps to separate incoming and outgoing messages
 */
@Data
@AllArgsConstructor
public class InOutEvent {
    /** Enumeration of In and Out types */
    public enum InOutType {
        IN, OUT
    }

    /** Type (incoming ot outgoing) */
    private InOutType type;

    /** Wrapped event */
    private Event event;
}
