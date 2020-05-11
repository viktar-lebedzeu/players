package org.vlebedzeu.players.api.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlebedzeu.players.api.events.DirectMessageEvent;
import org.vlebedzeu.players.api.events.Event;
import org.vlebedzeu.players.api.events.MessageEvent;
import org.vlebedzeu.players.api.events.ReadyEvent;
import org.vlebedzeu.players.api.events.ShootDownEvent;
import org.vlebedzeu.players.api.events.SubscribeEvent;
import org.vlebedzeu.players.api.events.UnsubscribeEvent;

/**
 *
 */
public class EventMessageMapperTest {
    private static final Logger logger = LoggerFactory.getLogger(EventMessageMapperTest.class);

    private final EventMessageMapper mapper = new EventMessageMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Test EventMessageMapper.eventToString() and EventMessageMapper.stringToEvent()")
    public void testEventMapping() {
        assertEvent(new ReadyEvent(null));
        assertEvent(new ShootDownEvent(null));
        assertEvent(new SubscribeEvent(RandomStringUtils.randomAlphabetic(10)));
        assertEvent(new UnsubscribeEvent(RandomStringUtils.randomAlphabetic(10)));

        assertEvent(new MessageEvent(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomPrint(100)));
        assertEvent(new MessageEvent(
                RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomPrint(100)));

        assertEvent(new DirectMessageEvent(
                RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomPrint(100)));
        assertEvent(new DirectMessageEvent(
                RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomPrint(100)));
    }

    @Test
    @DisplayName("Test incorrect values")
    public void testIncorrectValues() throws Exception {
        Assertions.assertNull(mapper.stringToEvent(RandomStringUtils.randomAlphanumeric(10)));

        MessageEnvelope envelope =
                new MessageEnvelope("wrong.package.WrongClass", RandomStringUtils.randomAlphanumeric(10));
        String envelopeString = objectMapper.writeValueAsString(envelope);
        Assertions.assertNull(mapper.stringToEvent(envelopeString));
    }

    private void assertEvent(Event event) {
        String eventMsg = mapper.eventToString(event);
        logger.info("event : {}", eventMsg);
        Event recovered = mapper.stringToEvent(eventMsg);
        Assertions.assertEquals(event, recovered);
    }
}
