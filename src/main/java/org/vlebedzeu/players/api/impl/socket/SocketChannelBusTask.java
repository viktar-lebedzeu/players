package org.vlebedzeu.players.api.impl.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlebedzeu.players.api.events.DirectMessageEvent;
import org.vlebedzeu.players.api.events.Event;
import org.vlebedzeu.players.api.events.InOutEvent;
import org.vlebedzeu.players.api.events.MessageEvent;
import org.vlebedzeu.players.api.events.ReadyEvent;
import org.vlebedzeu.players.api.events.ShootDownEvent;
import org.vlebedzeu.players.api.events.SubscribeEvent;
import org.vlebedzeu.players.api.events.SubscriptionAware;
import org.vlebedzeu.players.api.events.UnsubscribeEvent;
import org.vlebedzeu.players.api.players.Player;
import org.vlebedzeu.players.api.socket.SocketConnection;

import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Channel event queue task for socket implementation
 */
public class SocketChannelBusTask implements Runnable {
    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(SocketChannelBusTask.class);

    /** Event queue */
    private final Queue<InOutEvent> eventQueue;
    /** Event queue semaphore */
    private final Semaphore semaphore;
    /** Socket connection (server or client) */
    private final SocketConnection conn;
    /** Player attached to current channel */
    private final Player player;

    /** Running flag */
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    /**
     * Parametrized constructor
     * @param eventQueue Events queue
     * @param semaphore Events queue semaphore
     * @param conn Socket connection instance
     * @param player Attached player
     */
    public SocketChannelBusTask(Queue<InOutEvent> eventQueue, Semaphore semaphore,
                                SocketConnection conn, Player player) {
        this.eventQueue = eventQueue;
        this.semaphore = semaphore;
        this.conn = conn;
        this.player = player;
    }

    /**
     * Starts event bus
     */
    @Override
    public void run() {
        while (isRunning.get()) {
            try {
                semaphore.acquire();
                while (!eventQueue.isEmpty()) {
                    processEvent(eventQueue.poll());
                }
            } catch (InterruptedException e) {
                // Ignoring this exception
            }
        }
        logger.info("Socket channel bus task completed.");
    }

    /**
     * Stops event bus
     */
    public void stop() {
        isRunning.set(false);
        if (semaphore.hasQueuedThreads()) {
            semaphore.release();
        }
    }

    /**
     * Processes event
     * @param inOutEvent Event to process
     */
    private void processEvent(InOutEvent inOutEvent) {
        if (inOutEvent == null) {
            return;
        }

        final InOutEvent.InOutType type = inOutEvent.getType();
        switch (type) {
            case IN:
                processInEvent(inOutEvent.getEvent());
                break;
            case OUT:
                processOutEvent(inOutEvent.getEvent());
                break;
        }
    }

    /**
     * Processes incoming event
     * @param event Incoming event
     */
    private void processInEvent(Event event) {
        switch (event.getType()) {
            case READY:
                player.handleReadyEvent((ReadyEvent) event);
                break;

            case SHOOT_DOWN:
                player.handleShootDownEvent((ShootDownEvent) event);
                if (!conn.isServerMode()) {
                    conn.stopNow();
                }
                break;

            case SUBSCRIBE:
                if (player instanceof SubscriptionAware) {
                    ((SubscriptionAware) player).handleSubscribeEvent((SubscribeEvent) event);
                }
                break;

            case UNSUBSCRIBE:
                if (player instanceof SubscriptionAware) {
                    ((SubscriptionAware) player).handleUnsubscribeEvent((UnsubscribeEvent) event);
                }
                break;

            case DIRECT_MESSAGE:
                player.handleMessageEvent((DirectMessageEvent) event);
                break;

            case MESSAGE:
                player.handleMessageEvent((MessageEvent) event);
                break;
        }
    }

    /**
     * Processes outgoing event
     * @param event Outgoing event
     */
    private void processOutEvent(Event event) {
        conn.sendEvent(event);
    }
}