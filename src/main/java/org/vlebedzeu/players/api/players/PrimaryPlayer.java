package org.vlebedzeu.players.api.players;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlebedzeu.players.api.Channel;
import org.vlebedzeu.players.api.MessageSource;
import org.vlebedzeu.players.api.events.MessageEvent;
import org.vlebedzeu.players.api.events.ReadyEvent;
import org.vlebedzeu.players.api.events.ShootDownEvent;
import org.vlebedzeu.players.api.events.SubscribeEvent;
import org.vlebedzeu.players.api.events.SubscriptionAware;
import org.vlebedzeu.players.api.events.UnsubscribeEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Primary player instance, aka "initiator"
 */
public class PrimaryPlayer extends Player implements SubscriptionAware {
    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(PrimaryPlayer.class);

    /** Communication channel */
    private final Channel channel;

    /** Message source */
    private final MessageSource messageSource;

    /** Set of other players Ids */
    @Getter
    private final Set<String> playerIds = Collections.synchronizedSet(new HashSet<>());

    /** Message Id, responses we waiting for */
    private final AtomicReference<String> awaitingMessageId = new AtomicReference<>();
    /** Set of player Ids that should response */
    private final Set<String> awaitingResponders = Collections.synchronizedSet(new HashSet<>());

    /** Currect message count */
    private final AtomicInteger count = new AtomicInteger(0);
    /** Limit of sent text messages */
    private final int limit;

    /**
     * Parametrized constructor
     * @param id Player Id
     * @param channel Communication channel
     * @param messageSource Message source
     * @param limit Limit of messages
     */
    public PrimaryPlayer(String id, Channel channel, MessageSource messageSource, int limit) {
        super(id);
        this.channel = channel;
        this.messageSource = messageSource;
        this.limit = limit;
    }

    @Override
    public void handleMessageEvent(MessageEvent event) {
        logger.info("Primary player \"{}\" received message \"{}\" from \"{}\"",
                this.id, event.getText(), event.getSenderId());
        if (StringUtils.equals(event.getSourceMessageId(), awaitingMessageId.get())) {
            awaitingResponders.remove(event.getSenderId());
            if (awaitingResponders.isEmpty()) {
                awaitingMessageId.set(null);
                if (count.incrementAndGet() < limit) {
                    // All responses were received - sending next
                    sendNextMessage();
                }
                else {
                    shootDownChannel();
                }
            }
        }
    }

    @Override
    public void handleReadyEvent(ReadyEvent event) {
        // Channel is ready, starting conversation
        logger.info("Primary player \"{}\": channel is ready. Starting conversation.", this.id);
        sendNextMessage();
    }

    @Override
    public void handleShootDownEvent(ShootDownEvent event) {
        // Handles shoot down
        logger.info("Primary player \"{}\" received shootdown event.", this.id);
    }

    @Override
    public void handleSubscribeEvent(SubscribeEvent event) {
        if (!StringUtils.equals(event.getSenderId(), id)) {
            playerIds.add(event.getSenderId());
        }
    }

    @Override
    public void handleUnsubscribeEvent(UnsubscribeEvent event) {
        playerIds.remove(event.getSenderId());
        awaitingResponders.remove(event.getSenderId());
        if (playerIds.isEmpty()) {
            logger.info("All secondary players unsubscribed. Shooting down the channel.");
            shootDownChannel();
        }
    }

    /**
     * Shoots down communication channel
     */
    private void shootDownChannel() {
        logger.info("Started shooting down the channel");
        channel.stop();
    }

    /**
     * Sends the next text message
     */
    private void sendNextMessage() {
        String msgId = awaitingMessageId.get();
        if (StringUtils.isBlank(msgId)) {
            MessageEvent event = new MessageEvent(getId(), messageSource.nextMessage());
            awaitingMessageId.set(event.getEventId());
            awaitingResponders.clear();
            awaitingResponders.addAll(playerIds);
            channel.broadcast(event);
        }
    }
}
