package org.vlebedzeu.players.api.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 */
public class ServerConnectionTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ServerConnectionTask.class);

    // private final ServerSocket serverSocket;
    // private final ArrayList<Socket> clientSockets = new ArrayList<>();
    private final AsynchronousServerSocketChannel serverChannel;
    private final SocketChannelHandler handler;

    /*
    public ServerConnectionTask(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    */

    public ServerConnectionTask(AsynchronousServerSocketChannel serverChannel, SocketChannelHandler handler) {
        this.serverChannel = serverChannel;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Future<AsynchronousSocketChannel> acceptResult = serverChannel.accept();
                AsynchronousSocketChannel clientChannel = acceptResult.get();
                handler.onSocketChannelOpen(new SocketHolder(handler, clientChannel));
            }
        } catch (InterruptedException | ExecutionException e) {
            // Execution interrupted. Shooting down.
            logger.info("Execution interrupted. Shooting down.");
        }
    }
}
