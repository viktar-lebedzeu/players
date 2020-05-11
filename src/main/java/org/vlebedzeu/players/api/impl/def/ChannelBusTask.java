package org.vlebedzeu.players.api.impl.def;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlebedzeu.players.api.events.DirectMessageEvent;
import org.vlebedzeu.players.api.events.Event;
import org.vlebedzeu.players.api.events.MessageEvent;
import org.vlebedzeu.players.api.events.ReadyEvent;
import org.vlebedzeu.players.api.events.ShootDownEvent;
import org.vlebedzeu.players.api.events.SubscribeEvent;
import org.vlebedzeu.players.api.events.SubscriptionAware;
import org.vlebedzeu.players.api.events.UnsubscribeEvent;
import org.vlebedzeu.players.api.players.Player;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class ChannelBusTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ChannelBusTask.class);

    private final Queue<Event> eventQueue;
    private final Semaphore semaphore;
    private final Set<Player> players;
    private final Map<String, Player> playersMap;
    private final Set<SubscriptionAware> subscribers;

    private AtomicBoolean isRunning = new AtomicBoolean(true);

    public ChannelBusTask(Queue<Event> eventQueue, Semaphore semaphore,
                          Set<Player> players, Map<String, Player> playersMap,
                          Set<SubscriptionAware> subscribers) {
        this.eventQueue = eventQueue;
        this.semaphore = semaphore;
        this.players = players;
        this.playersMap = playersMap;
        this.subscribers = subscribers;
    }

    public void stop() {
        isRunning.set(false);
        if (semaphore.hasQueuedThreads()) {
            semaphore.release();
        }
    }

    @Override
    public void run() {
        while(isRunning.get()) {
            try {
                semaphore.acquire();
                while (!eventQueue.isEmpty()) {
                    processEvent(eventQueue.poll());
                }
            } catch (InterruptedException e) {
                // Ignoring this exception
            }
        }
        logger.info("Channel bus task completed.");
    }

    private void processEvent(Event event) {
        if (event == null) {
            return;
        }

        switch (event.getType()) {
            case READY:
                players.forEach(player -> player.handleReadyEvent((ReadyEvent) event));
                break;

            case SHOOT_DOWN:
                players.forEach(player -> player.handleShootDownEvent((ShootDownEvent) event));
                break;

            case SUBSCRIBE:
                subscribers.parallelStream()
                        .forEach(s -> s.handleSubscribeEvent((SubscribeEvent) event));
                break;

            case UNSUBSCRIBE:
                subscribers.parallelStream()
                        .forEach(s -> s.handleUnsubscribeEvent((UnsubscribeEvent) event));
                break;

            case DIRECT_MESSAGE:
                // Handling direct messages
                DirectMessageEvent directMessageEvent = (DirectMessageEvent) event;
                String destId = directMessageEvent.getDestinationPlayerId();
                if (!destId.equals(event.getSenderId()) && playersMap.containsKey(destId)) {
                    playersMap.get(destId).handleMessageEvent((MessageEvent) event);
                }
                break;

            case MESSAGE:
                // Handling broadcast event
                players.parallelStream()
                        .filter(sub -> !sub.getId().equals(event.getSenderId()))
                        .forEach(sub -> {
                                    sub.handleMessageEvent((MessageEvent) event);
                                }
                        );
                break;
        }
    }
}
