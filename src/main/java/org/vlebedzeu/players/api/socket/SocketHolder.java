package org.vlebedzeu.players.api.socket;

import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Socket holder that helps to handle all socket events
 */
public class SocketHolder implements Runnable {
    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(SocketHolder.class);

    /** Socket channel handler */
    private final SocketChannelHandler handler;

    /** Async socket channel */
    @Getter
    private final AsynchronousSocketChannel clientChannel;

    /** Holder Id */
    @Getter
    private final String id;

    /**
     * Parametrized constructor
     * @param handler Socket channel handler
     * @param clientChannel Socket channel
     */
    public SocketHolder(SocketChannelHandler handler, AsynchronousSocketChannel clientChannel) {
        this.handler = handler;
        this.clientChannel = clientChannel;
        this.id = RandomStringUtils.randomAlphanumeric(50);
    }

    @Override
    public void run() {
        readMessages();
    }

    /**
     * Closes related socket channel
     */
    public void close() {
        // Closes connection
        if (clientChannel != null && clientChannel.isOpen()) {
            try {
                clientChannel.close();
            } catch (IOException e) {
                // Skipping this exception
            }

        }
    }

    /**
     * Sends string message into socket channel
     * @param message String message
     */
    public void sendMessage(String message) {
        try {
            // Writing size of message
            String lengthString = String.format("%08X", message.length());
            ByteBuffer szBuffer = ByteBuffer.wrap(lengthString.getBytes());
            Future<Integer> writeSzResult = clientChannel.write(szBuffer);
            szBuffer.flip();
            Integer wrote = writeSzResult.get();

            // Writing message body
            byte[] byteMsg = message.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(byteMsg);
            Future<Integer> writeResult = clientChannel.write(buffer);
            buffer.flip();

            wrote = writeResult.get();
        } catch (InterruptedException | ExecutionException e) {
            //
            handler.onSocketChannelClosed(this);
        }
    }

    /**
     * Reads messages in the loop
     */
    private void readMessages() {
        if ((clientChannel != null) && (clientChannel.isOpen())) {
            ByteBuffer szBuffer = ByteBuffer.allocate(8);
            boolean isRunning = true;

            while(isRunning) {
                try {
                    Future<Integer> readSzResult = clientChannel.read(szBuffer);
                    Integer szResult = readSzResult.get();
                    szBuffer.flip();
                    int messageSize = Integer.parseInt(new String(szBuffer.array()), 16);
                    int bufferSize = Math.min(messageSize, 1024);

                    ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
                    StringBuilder sb = new StringBuilder();

                    while (sb.length() < messageSize) {
                        Future<Integer> readResult  = clientChannel.read(buffer);

                        Integer result = readResult.get();
                        if (result > 0) {
                            byte[] byteArray = (result < buffer.capacity())
                                    ? Arrays.copyOfRange(buffer.array(), 0, result)
                                    : buffer.array();
                            sb.append(new String(byteArray));
                            buffer.flip();
                        }
                    }
                    if (sb.length() > 0) {
                        handler.onMessage(this, sb.toString());
                    }
                } catch (InterruptedException | ExecutionException e) {
                    //
                    logger.info("Socket {} lost connection", id);
                    isRunning = false;
                    handler.onSocketChannelClosed(this);
                }
            }
        }
    }
}
