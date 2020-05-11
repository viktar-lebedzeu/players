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

    @JsonProperty(value = "class")
    private String className;

    @JsonProperty(value = "payload")
    private Object payload;
}
