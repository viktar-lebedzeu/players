package org.vlebedzeu.players.api.impl.socket;

/**
 *
 */
public class TimeoutTask implements Runnable {
    private final InitTimeoutTrigger trigger;

    public TimeoutTask(InitTimeoutTrigger trigger) {
        this.trigger = trigger;
    }

    @Override
    public void run() {
        trigger.onInitTimeout();
    }
}
