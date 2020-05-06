package org.vlebedzeu.players.api.players;

import lombok.Getter;
import org.vlebedzeu.players.api.MessageSource;
import org.vlebedzeu.players.api.events.ChannelStateAware;
import org.vlebedzeu.players.api.events.MessageAware;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Player entity class
 */
public abstract class Player implements MessageAware, ChannelStateAware {
    @Getter
    protected final String id;

    @Getter
    private final AtomicReference<MessageSource> messageSource = new AtomicReference<>();

    public Player(String id) {
        this.id = id;
    }

    public abstract boolean isPrimary();
}
