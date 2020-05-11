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
 *
 */
public class SocketHolder implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SocketHolder.class);

    // private final Socket socket;
    private final SocketChannelHandler handler;

    @Getter
    private final AsynchronousSocketChannel clientChannel;

    @Getter
    private final String id;

/*
    public ServerSocketHolder(Socket socket) {
        this.socket = socket;
    }
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

    public void sendMessage(String message) {
        try {
            // Writing size of message
            String lengthString = String.format("%08X", message.length());
            ByteBuffer szBuffer = ByteBuffer.wrap(lengthString.getBytes());
            Future<Integer> writeSzResult = clientChannel.write(szBuffer);
            szBuffer.flip();
            Integer wrote = writeSzResult.get();

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
