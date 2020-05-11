package org.vlebedzeu.players.api.players;

import lombok.Getter;
import org.vlebedzeu.players.api.events.ChannelStateAware;
import org.vlebedzeu.players.api.events.MessageAware;

/**
 * Base Player entity class
 */
public abstract class Player implements MessageAware, ChannelStateAware {
    /** Player Id */
    @Getter
    protected final String id;

    /**
     * Parametrized constructor
     * @param id Player Id
     */
    public Player(String id) {
        this.id = id;
    }
}
