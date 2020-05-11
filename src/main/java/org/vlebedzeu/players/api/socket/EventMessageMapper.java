package org.vlebedzeu.players.api.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.vlebedzeu.players.api.events.Event;

/**
 * Mapper for converting event object to string and vice versa. Used in message exchange
 */
@NoArgsConstructor
public class EventMessageMapper {
    /** JSON object mapper */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts the given mapper into message envelope string
     * @param event Event to convert
     * @return String of converted event
     */
    public String eventToString(Event event) {
        String className = event.getClass().getCanonicalName();
        MessageEnvelope envelope = new MessageEnvelope(className, event);
        try {
            return objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * Converts string of message envelope into the event
     * @param str String to convert
     * @return Converted event
     */
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
            // Skipping this exception
        }
        return null;
    }
}
