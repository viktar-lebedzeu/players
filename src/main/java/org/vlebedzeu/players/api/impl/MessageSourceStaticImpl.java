package org.vlebedzeu.players.api.impl;

import org.vlebedzeu.players.api.MessageSource;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of MessageSource interface that always returns predefined message(s)
 */
public class MessageSourceStaticImpl implements MessageSource {
    private final List<String> messages;

    private final AtomicInteger idx = new AtomicInteger(0);

    public MessageSourceStaticImpl(String... messages) {
        this.messages = Arrays.asList(messages);
    }

    @Override
    public String nextMessage() {
        if (messages.size() == 1) {
            return messages.get(0);
        }
        if (idx.get() >= messages.size()) {
            idx.set(0);
        }
        return messages.get(idx.getAndIncrement());
    }
}
