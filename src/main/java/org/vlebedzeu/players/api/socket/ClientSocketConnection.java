package org.vlebedzeu.players.api.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlebedzeu.players.api.Channel;
import org.vlebedzeu.players.api.events.Event;
import org.vlebedzeu.players.api.events.EventType;
import org.vlebedzeu.players.api.events.InOutEvent;
import org.vlebedzeu.players.api.events.ShootDownEvent;

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
 *
 */
public class ClientSocketConnection implements SocketConnection, SocketChannelHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientSocketConnection.class);

    private final String host;
    private final int port;
    private final SocketEventQueueAware queueAware;

    private SocketHolder holder;

    private final EventMessageMapper mapper = new EventMessageMapper();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

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
        // logger.info("Received new message: {}", message);
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
