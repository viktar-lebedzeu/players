package org.vlebedzeu.players.api.socket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message envelope for exchanging events data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageEnvelope {
    /** Class name of wrapped event */
    @JsonProperty(value = "class")
    private String className;

    /** Wrapped event as envelope's payload */
    @JsonProperty(value = "payload")
    private Object payload;
}
