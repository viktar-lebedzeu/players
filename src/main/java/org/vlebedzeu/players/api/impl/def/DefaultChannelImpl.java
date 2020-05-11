package org.vlebedzeu.players.api.impl.def;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlebedzeu.players.api.Channel;
import org.vlebedzeu.players.api.InitializationException;
import org.vlebedzeu.players.api.MessageSource;
import org.vlebedzeu.players.api.events.DirectMessageEvent;
import org.vlebedzeu.players.api.events.Event;
import org.vlebedzeu.players.api.events.MessageEvent;
import org.vlebedzeu.players.api.events.ReadyEvent;
import org.vlebedzeu.players.api.events.ShootDownEvent;
import org.vlebedzeu.players.api.events.SubscribeEvent;
import org.vlebedzeu.players.api.events.SubscriptionAware;
import org.vlebedzeu.players.api.events.UnsubscribeEvent;
import org.vlebedzeu.players.api.players.Player;
import org.vlebedzeu.players.api.players.PrimaryPlayer;
import org.vlebedzeu.players.api.players.SecondaryPlayer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Default implementation of channel interface
 */
public class DefaultChannelImpl implements Channel {
    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(DefaultChannelImpl.class);

    /** Set of players */
    private final Set<Player> players = Collections.synchronizedSet(new HashSet<>());
    /** Map of player Id and player object */
    private final Map<String, Player> playersMap = Collections.synchronizedMap(new HashMap<>());
    /** Set of subscribers */
    private final Set<SubscriptionAware> subscribers = Collections.synchronizedSet(new HashSet<>());
    /** Events queue */
    private final ConcurrentLinkedQueue<Event> events = new ConcurrentLinkedQueue<>();
    /** Channel bus executor */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    /** Events queue semaphore */
    private final Semaphore semaphore = new Semaphore(1);
    /** Channel bus task */
    private ChannelBusTask queueTask;

    @Override
    public void start() throws InitializationException {
        queueTask = new ChannelBusTask(events, semaphore, players, playersMap, subscribers);
        executor.submit(queueTask);
        addEventAndUnlock(new ReadyEvent(null));
    }

    @Override
    public void stop() {
        addEventAndUnlock(new ShootDownEvent(null));
        queueTask.stop();
        executor.shutdown();
    }

    @Override
    public void subscribePlayer(Player player) {
        players.add(player);
        playersMap.put(player.getId(), player);
        if (player instanceof SubscriptionAware) {
            subscribers.add((SubscriptionAware) player);
        }

        addEventAndUnlock(new SubscribeEvent(player.getId()));
    }

    @Override
    public void unsubscribePlayer(Player player) {
        if (player instanceof SubscriptionAware) {
            subscribers.remove(player);
        }
        addEventAndUnlock(new UnsubscribeEvent(player.getId()));

        players.remove(player);
        playersMap.remove(player.getId());
    }

    @Override
    public synchronized void broadcast(MessageEvent event) {
        addEventAndUnlock(event);
    }

    @Override
    public synchronized void send(DirectMessageEvent event) {
        addEventAndUnlock(event);
    }

    /**
     * Initialize primary player
     * @param playerId Player Id
     * @param messageSource Message source
     * @param limit Limit of sending messages
     */
    public void initPrimaryPlayer(String playerId, MessageSource messageSource, int limit) {
        PrimaryPlayer player = new PrimaryPlayer(playerId, this, messageSource, limit);
        subscribePlayer(player);
    }

    /**
     * Initializes secondary players
     * @param playerIds Players Ids
     */
    public void initSecondaryPlayers(List<String> playerIds) {
        playerIds.forEach(id -> {
                SecondaryPlayer player = new SecondaryPlayer(id, this);
                subscribePlayer(player);
        });
    }

    /**
     * Adds event into the queue and unlock processing
     * @param event Event to add
     */
    private void addEventAndUnlock(Event event) {
        events.add(event);
        if (semaphore.hasQueuedThreads()) {
            semaphore.release();
        }
    }
}
