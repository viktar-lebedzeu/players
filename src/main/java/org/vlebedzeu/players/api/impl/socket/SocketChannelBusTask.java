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
import org.vlebedzeu.players.api.players.SecondaryPlayer;
import org.vlebedzeu.players.api.socket.SocketConnection;

import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class SocketChannelBusTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SocketChannelBusTask.class);

    private final Queue<InOutEvent> eventQueue;
    private final Semaphore semaphore;
    private final SocketConnection conn;
    private final Player player;

    private AtomicBoolean isRunning = new AtomicBoolean(true);

    public SocketChannelBusTask(Queue<InOutEvent> eventQueue, Semaphore semaphore,
                                SocketConnection conn, Player player) {
        this.eventQueue = eventQueue;
        this.semaphore = semaphore;
        this.conn = conn;
        this.player = player;
    }

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

    public void stop() {
        isRunning.set(false);
        if (semaphore.hasQueuedThreads()) {
            semaphore.release();
        }
    }

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

    private void processOutEvent(Event event) {
        conn.sendEvent(event);
    }
}