package org.vlebedzeu.players.api.impl.socket;

/**
 * Timeout task that triggers start of conversation
 */
public class TimeoutTask implements Runnable {
    /** Object that must be triggered */
    private final InitTimeoutTrigger trigger;

    /**
     * Parametrized constructor
     * @param trigger Trigger object
     */
    public TimeoutTask(InitTimeoutTrigger trigger) {
        this.trigger = trigger;
    }

    @Override
    public void run() {
        trigger.onInitTimeout();
    }
}
