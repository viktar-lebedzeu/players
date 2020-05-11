package org.vlebedzeu.players.api.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlebedzeu.players.api.events.Event;

/**
 *
 */
@NoArgsConstructor
public class EventMessageMapper {
    private static final Logger logger = LoggerFactory.getLogger(EventMessageMapper.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String eventToString(Event event) {
        String className = event.getClass().getCanonicalName();
        MessageEnvelope envelope = new MessageEnvelope(className, event);
        try {
            return objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public Event stringToEvent(String str) {
        try {
            if (StringUtils.isNotBlank(str)) {
                MessageEnvelope envelope = objectMapper.readValue(str, MessageEnvelope.class);
                String className = envelope.getClassName();
                Class<?> clazz = Class.forName(className);
                String payload = objectMapper.writeValueAsString(envelope.getPayload());
                return (Event) objectMapper.readValue(payload, clazz);
            }
        } catch (JsonProcessingException | ClassNotFoundException e) {
            //
        }
        return null;
    }
}
