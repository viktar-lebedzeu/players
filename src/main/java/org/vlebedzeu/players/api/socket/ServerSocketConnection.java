package org.vlebedzeu.players.api.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlebedzeu.players.api.events.DirectMessageEvent;
import org.vlebedzeu.players.api.events.Event;
import org.vlebedzeu.players.api.events.InOutEvent;
import org.vlebedzeu.players.api.events.SubscribeEvent;
import org.vlebedzeu.players.api.events.UnsubscribeEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.vlebedzeu.players.api.events.InOutEvent.InOutType.IN;

/**
 * Implementation of server socket connection
 */
public class ServerSocketConnection implements SocketConnection, SocketChannelHandler {
    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(ServerSocketConnection.class);

    /** Connection port */
    private final int port;
    /** Socket event queue interface */
    private final SocketEventQueueAware queueAware;

    /** Async server socket channel */
    private AsynchronousServerSocketChannel serverChannel;
    /** Server connection task */
    private ServerConnectionTask serverConnectionTask;

    /** Executor for server connection task */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    /** Executor for newly added client socket connections */
    private final ExecutorService clientExecutor = Executors.newCachedThreadPool();

    /** Map of socket holder Id and its object */
    private final Map<String, SocketHolder> socketHolderMap = Collections.synchronizedMap(new HashMap<>());
    /** Map of subscribed player Id and socket holder object */
    private final Map<String, SocketHolder> playerSocketHolderMap = Collections.synchronizedMap(new HashMap<>());
    /** Map of socket holder Id and player Id */
    private final Map<String, String> socketHolderPlayerMap = Collections.synchronizedMap(new HashMap<>());

    /** Event message mapper */
    private final EventMessageMapper mapper = new EventMessageMapper();

    /**
     * Parametrized constructor
     * @param port Port
     * @param queueAware Socket event queue interface
     */
    public ServerSocketConnection(int port, SocketEventQueueAware queueAware) {
        this.port = port;
        this.queueAware = queueAware;
    }

    /**
     * Starts server socket connection
     * @return Reference to started connection
     * @throws IOException In case of any IO error
     */
    public SocketConnection start() throws IOException {
        serverChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
        logger.info("Run application connection in server mode. Bind to port {}", port);

        serverConnectionTask = new ServerConnectionTask(serverChannel, this);
        executor.submit(serverConnectionTask);

        return this;
    }

    @Override
    public void stop() {
        if (serverChannel != null && serverChannel.isOpen()) {
            try {
                serverChannel.close();
            }
            catch(IOException e) {
                // Skipping this exception
            }
        }

        serverConnectionTask.stop();
        executor.shutdown();
        clientExecutor.shutdown();
        socketHolderMap.values()
                .parallelStream()
                .forEach(SocketHolder::close);
        socketHolderMap.clear();
        socketHolderPlayerMap.clear();
        playerSocketHolderMap.clear();
    }

    @Override
    public void stopNow() {
        stop();
    }

    @Override
    public void onSocketChannelOpen(SocketHolder channelHolder) {
        //
        logger.info("Socket opened : {}", channelHolder.getId());
        socketHolderMap.put(channelHolder.getId(), channelHolder);
        clientExecutor.submit(channelHolder);
    }

    @Override
    public void onSocketChannelClosed(SocketHolder channelHolder) {
        //
        String channelHolderId = channelHolder.getId();
        logger.info("Socket closed : {}", channelHolderId);

        if (socketHolderPlayerMap.containsKey(channelHolderId)) {
            String playerId = socketHolderPlayerMap.remove(channelHolderId);
            playerSocketHolderMap.remove(playerId);
        }
        socketHolderMap.remove(channelHolderId);
    }

    @Override
    public void onMessage(SocketHolder channelHolder, String message) {
        //
        // logger.info("Received new message: {}", message);
        Event event = mapper.stringToEvent(message);
        if (event != null) {
            switch (event.getType()) {
                case SUBSCRIBE:
                    SubscribeEvent subscribeEvent = (SubscribeEvent) event;
                    String subscriberId = subscribeEvent.getSenderId();
                    playerSocketHolderMap.put(subscriberId, channelHolder);
                    socketHolderPlayerMap.put(channelHolder.getId(), subscriberId);

                    logger.info("Player {} subscribed.", subscriberId);
                    queueAware.playerSubscribed(subscribeEvent);
                    break;

                case UNSUBSCRIBE:
                    UnsubscribeEvent unsubscribeEvent = (UnsubscribeEvent) event;
                    String unsubscriberId = unsubscribeEvent.getSenderId();
                    playerSocketHolderMap.remove(unsubscriberId);
                    socketHolderPlayerMap.remove(channelHolder.getId());
                    socketHolderMap.remove(channelHolder.getId());
                    channelHolder.close();

                    logger.info("Player {} unsubscribed.", unsubscriberId);
                    queueAware.playerUnsubscribed(unsubscribeEvent);
                    break;
            }
            queueAware.addEventAndUnlock(new InOutEvent(IN, event));
        }
    }

    @Override
    public void sendEvent(Event event) {
        if (event != null) {
            switch (event.getType()) {
                case SUBSCRIBE:
                case UNSUBSCRIBE:
                case SHOOT_DOWN:
                case MESSAGE:
                    playerSocketHolderMap.keySet()
                            .parallelStream()
                            .forEach(p -> sendEvent(p, event));
                    break;

                case DIRECT_MESSAGE:
                    DirectMessageEvent directMessageEvent = (DirectMessageEvent) event;
                    sendEvent(directMessageEvent.getDestinationPlayerId(), event);
                    break;
            }
        }
    }

    @Override
    public boolean isServerMode() {
        return true;
    }

    /**
     * Send event to specified player
     * @param playerId Player Id
     * @param event Event to send
     */
    private void sendEvent(final String playerId, Event event) {
        if (playerSocketHolderMap.containsKey(playerId)) {
            playerSocketHolderMap.get(playerId).sendMessage(mapper.eventToString(event));
        }
    }
}
