package org.vlebedzeu.players.api.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlebedzeu.players.api.events.Event;
import org.vlebedzeu.players.api.events.InOutEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.vlebedzeu.players.api.events.InOutEvent.InOutType.IN;

/**
 * Implementation of client socket connection
 */
public class ClientSocketConnection implements SocketConnection, SocketChannelHandler {
    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(ClientSocketConnection.class);

    /** Connection host */
    private final String host;
    /** Connection port */
    private final int port;
    /** Event queue interface */
    private final SocketEventQueueAware queueAware;

    /** Socket holder */
    private SocketHolder holder;

    /** Event mapper */
    private final EventMessageMapper mapper = new EventMessageMapper();
    /** Executor for socket exchange handler */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Parametrized constructor
     * @param host Communication host
     * @param port Communication port
     * @param queueAware Socket event queue interface instance
     */
    public ClientSocketConnection(String host, int port, SocketEventQueueAware queueAware) {
        this.host = host;
        this.port = port;
        this.queueAware = queueAware;
    }

    @Override
    public SocketConnection start() throws IOException {
        try {
            AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
            channel.connect(new InetSocketAddress(host, port)).get(1, TimeUnit.MINUTES);
            holder = new SocketHolder(this, channel);
            onSocketChannelOpen(holder);

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new IOException(e);
        }
        logger.info("Run application connection in client mode. Bind to host {}:{}", host, port);

        return this;
    }

    @Override
    public void stop() {
        if (holder != null) {
            holder.close();
        }
        executor.shutdown();
    }

    @Override
    public void stopNow() {
        queueAware.stop();
    }

    @Override
    public void onSocketChannelOpen(SocketHolder channelHolder) {
        logger.info("Socket opened : {}", channelHolder.getId());
        executor.submit(channelHolder);
    }

    @Override
    public void onSocketChannelClosed(SocketHolder channelHolder) {
        logger.info("Socket closed : {}", channelHolder.getId());
        stopNow();
    }

    @Override
    public void onMessage(SocketHolder channelHolder, String message) {
        Event event = mapper.stringToEvent(message);
        if (event != null) {
            queueAware.addEventAndUnlock(new InOutEvent(IN, event));
        }
    }

    @Override
    public void sendEvent(Event event) {
        holder.sendMessage(mapper.eventToString(event));
    }

    @Override
    public boolean isServerMode() {
        return false;
    }
}
