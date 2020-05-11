package org.vlebedzeu.players.api.players;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlebedzeu.players.api.MessageSender;
import org.vlebedzeu.players.api.events.DirectMessageEvent;
import org.vlebedzeu.players.api.events.MessageEvent;
import org.vlebedzeu.players.api.events.ReadyEvent;
import org.vlebedzeu.players.api.events.ShootDownEvent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of behaviour of secondary player. Sends concatenated received text message with current counter value.
 */
public class SecondaryPlayer extends Player {
    private static final Logger logger = LoggerFactory.getLogger(SecondaryPlayer.class);

    private final MessageSender messageSender;

    private final AtomicLong counter = new AtomicLong(0L);

    public SecondaryPlayer(String id, MessageSender messageSender) {
        super(id);
        this.messageSender = messageSender;
    }

    @Override
    public boolean isPrimary() {
        return false;
    }

    @Override
    public void handleMessageEvent(MessageEvent event) {
        String msg = event.getText();
        logger.info("Secondary player \"{}\" received message: \"{}\"", this.id, msg);

        DirectMessageEvent response = new DirectMessageEvent(this.id, event.getEventId(),
                msg + " " + counter.incrementAndGet(), event.getSenderId());
        messageSender.send(response);
    }

    @Override
    public void handleReadyEvent(ReadyEvent event) {
        logger.info("Secondary player \"{}\" received channel ready event.", this.id);
    }

    @Override
    public void handleShootDownEvent(ShootDownEvent event) {
        logger.info("Secondary player \"{}\" received shoot down event.", this.id);
    }
}
