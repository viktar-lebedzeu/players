package org.vlebedzeu.players.api.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Server socket connection task. Handles opening of new client socket connections.
 */
public class ServerConnectionTask implements Runnable {
    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(ServerConnectionTask.class);

    /** Reference to async server socket channel */
    private final AsynchronousServerSocketChannel serverChannel;
    /** Instance of socket channel handler */
    private final SocketChannelHandler handler;

    /** Running flag */
    private boolean isRunning = true;

    /**
     * Parametrized constructor
     * @param serverChannel Server socket channel
     * @param handler Socket channel handler
     */
    public ServerConnectionTask(AsynchronousServerSocketChannel serverChannel, SocketChannelHandler handler) {
        this.serverChannel = serverChannel;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                Future<AsynchronousSocketChannel> acceptResult = serverChannel.accept();
                AsynchronousSocketChannel clientChannel = acceptResult.get();
                handler.onSocketChannelOpen(new SocketHolder(handler, clientChannel));
            }
        } catch (InterruptedException | ExecutionException e) {
            // Execution interrupted. Shooting down.
            logger.info("Execution interrupted. Shooting down.");
        }
    }

    /**
     * Stops task execution
     */
    public void stop() {
        isRunning = false;
    }
}
