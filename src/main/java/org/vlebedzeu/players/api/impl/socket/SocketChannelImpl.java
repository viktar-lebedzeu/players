package org.vlebedzeu.players.api.impl.socket;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlebedzeu.players.api.Channel;
import org.vlebedzeu.players.api.InitializationException;
import org.vlebedzeu.players.api.MessageSource;
import org.vlebedzeu.players.api.events.DirectMessageEvent;
import org.vlebedzeu.players.api.events.InOutEvent;
import org.vlebedzeu.players.api.events.MessageEvent;
import org.vlebedzeu.players.api.events.ReadyEvent;
import org.vlebedzeu.players.api.events.ShootDownEvent;
import org.vlebedzeu.players.api.events.SubscribeEvent;
import org.vlebedzeu.players.api.events.UnsubscribeEvent;
import org.vlebedzeu.players.api.players.Player;
import org.vlebedzeu.players.api.players.PrimaryPlayer;
import org.vlebedzeu.players.api.players.SecondaryPlayer;
import org.vlebedzeu.players.api.socket.ClientSocketConnection;
import org.vlebedzeu.players.api.socket.ServerSocketConnection;
import org.vlebedzeu.players.api.socket.SocketConnection;
import org.vlebedzeu.players.api.socket.SocketEventQueueAware;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.vlebedzeu.players.api.events.InOutEvent.InOutType.*;

/**
 * Channel that implements socket connections
 */
public class SocketChannelImpl implements Channel, SocketEventQueueAware, InitTimeoutTrigger {
    private static final Logger logger = LoggerFactory.getLogger(SocketChannelImpl.class);

    private final String host;
    private final int port;
    private SocketConnection conn;
    private Player player;

    private String playerId;
    private MessageSource messageSource;
    private int limit;

    private final ConcurrentLinkedQueue<InOutEvent> events = new ConcurrentLinkedQueue<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService timeoutExecutor = Executors.newScheduledThreadPool(1);
    private final Semaphore semaphore = new Semaphore(1);
    private SocketChannelBusTask queueTask;

    @Getter
    private boolean serverMode = false;

    private int triggerSubscribers = Integer.MAX_VALUE;
    private long startTimeoutSecs = 180L;

    private boolean isTriggered = false;

    public SocketChannelImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() throws InitializationException {
        // Trying to initialize server socket connection
        try {
            conn = new ServerSocketConnection(port, this).start();
            serverMode = true;
        } catch (IOException e) {
            // Just logging this exception and trying to run client connection
            // logger.error("Caught IOException", e);
            logger.warn("Can not run in server mode. Port {} already in use.", port);
        }
        if (conn == null) {
            try {
                conn = new ClientSocketConnection(host, port, this).start();
            } catch (IOException e) {
                // Just logging this exception and trying to run client connection
                logger.error("Caught IOException", e);
            }
        }
        if (conn == null) {
            throw new InitializationException("Can not start application neither in server or client mode.");
        }

        if (serverMode) {
            initPrimaryPlayer();
            timeoutExecutor.schedule(new TimeoutTask(this), startTimeoutSecs, TimeUnit.SECONDS);
        }
        else {
            initSecondaryPlayer();
        }
        queueTask = new SocketChannelBusTask(events, semaphore, conn, player);
        executor.submit(queueTask);
    }

    @Override
    public void stop() {
//        if (serverMode) {
//            addEventAndUnlock(new InOutEvent(OUT, new ShootDownEvent(null)));
//        }
        queueTask.stop();
        executor.shutdown();
        if (!timeoutExecutor.isShutdown()) {
            timeoutExecutor.shutdown();
        }

        if (conn != null) {
            conn.stop();
        }
    }

    @Override
    public void subscribePlayer(Player player) {
        if (!serverMode) {
            addEventAndUnlock(new InOutEvent(OUT, new SubscribeEvent(player.getId())));
        }
    }

    @Override
    public void unsubscribePlayer(Player player) {
        if (!serverMode) {
            addEventAndUnlock(new InOutEvent(OUT, new UnsubscribeEvent(player.getId())));
        }
    }

    @Override
    public synchronized void broadcast(MessageEvent event) {
        addEventAndUnlock(new InOutEvent(OUT, event));
    }

    @Override
    public synchronized void send(DirectMessageEvent event) {
        addEventAndUnlock(new InOutEvent(OUT, event));
    }

    public void initStarter(long startTimeoutSecs, int triggerSubscribers) {
        this.startTimeoutSecs = startTimeoutSecs;
        this.triggerSubscribers = triggerSubscribers;
    }

    @Override
    public void onInitTimeout() {
        if (serverMode) {
            PrimaryPlayer primaryPlayer = (PrimaryPlayer) player;
            Set<String> playerIds = primaryPlayer.getPlayerIds();
            if (playerIds.size() > 0) {
                addEventAndUnlock(new InOutEvent(IN, new ReadyEvent(null)));
                timeoutExecutor.shutdown();
            }
            else {
                logger.info("There is no subscribers to start a conversation. Shooting down.");
                stop();
            }
        }
    }

    public void initPlayer(String playerId, MessageSource messageSource, int limit) {
        this.playerId = playerId;
        this.messageSource = messageSource;
        this.limit = limit;
    }

    private void initPrimaryPlayer() {
        player = new PrimaryPlayer(playerId, this, messageSource, limit);
        subscribePlayer(player);
    }

    private void initSecondaryPlayer() {
        player = new SecondaryPlayer(playerId, this);
        subscribePlayer(player);
        addEventAndUnlock(new InOutEvent(IN, new ReadyEvent(null)));
    }

    @Override
    public void addEventAndUnlock(InOutEvent event) {
        events.add(event);
        if (semaphore.hasQueuedThreads()) {
            semaphore.release();
        }
    }

    @Override
    public void playerSubscribed(SubscribeEvent event) {
        if (serverMode) {
            PrimaryPlayer primaryPlayer = (PrimaryPlayer) player;
            primaryPlayer.handleSubscribeEvent(event);

            if (!isTriggered) {
                Set<String> playerIds = primaryPlayer.getPlayerIds();
                if (playerIds.size() >= triggerSubscribers) {
                    addEventAndUnlock(new InOutEvent(IN, new ReadyEvent(null)));
                    timeoutExecutor.shutdown();
                    isTriggered = true;
                }
            }
        }
    }

    @Override
    public void playerUnsubscribed(UnsubscribeEvent event) {
        if (serverMode) {
            PrimaryPlayer primaryPlayer = (PrimaryPlayer) player;
            primaryPlayer.handleUnsubscribeEvent(event);
        }
    }
}
