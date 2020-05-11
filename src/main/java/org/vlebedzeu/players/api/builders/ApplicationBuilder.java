package org.vlebedzeu.players.api.builders;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.vlebedzeu.players.api.Channel;
import org.vlebedzeu.players.api.InitializationException;
import org.vlebedzeu.players.api.MessageSource;
import org.vlebedzeu.players.api.impl.def.DefaultChannelImpl;
import org.vlebedzeu.players.api.impl.MessageSourceStaticImpl;
import org.vlebedzeu.players.api.impl.socket.SocketChannelImpl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.vlebedzeu.players.api.ConfigConstants.*;

/**
 * Application builder class that helps to create and run application using command line options
 */
public class ApplicationBuilder {
    private static final String CHANNEL_TYPE_SOCKET = "socket";
    private final CommandLine cmdLine;
    private Channel channel;

    /**
     * Parametrized constructor
     * @param cmdLine Command line options
     */
    public ApplicationBuilder(CommandLine cmdLine) {
        this.cmdLine = cmdLine;
    }

    /**
     * Builds application instance
     * @return Reference to current instance to be able to build chain of method calls
     * @throws InitializationException In case of any initialization error
     */
    public ApplicationBuilder build() throws InitializationException {
        // Check for different types of channels
        channel = createChannel();
        return this;
    }

    /**
     * Starts application
     * @throws InitializationException In case of any initialization error
     */
    public void start() throws InitializationException {
        channel.start();
    }

    /**
     * Creates communication channel using command line options
     * @return Instance of communication channel
     * @throws InitializationException In case of any initialization error
     */
    private Channel createChannel() throws InitializationException {
        final String channelOpt = cmdLine.getOptionValue(OPT_CHANNEL);
        final String messageOpt = StringUtils.defaultIfBlank(cmdLine.getOptionValue(OPT_MESSAGE), "Default message");
        final String playersOpt = cmdLine.getOptionValue(OPT_PLAYERS);

        String[] messages = StringUtils.split(messageOpt, ",");
        MessageSourceStaticImpl messageSource = new MessageSourceStaticImpl(messages);

        List<String> playerIds = Arrays.stream(StringUtils.split(playersOpt, ","))
                .collect(Collectors.toList());

        if (StringUtils.startsWithIgnoreCase(channelOpt, CHANNEL_TYPE_SOCKET)) {
            return createSocketChannel(channelOpt, playerIds, messageSource);
        }
        return createDefaultChannel(playerIds, messageSource);
    }

    /**
     * Creates default channel implementation (in-memory, all players in the same java process)
     * @param playerIds List of players Ids to build communication environment
     * @param messageSource Message source that provides messages to send by primary player (aka "initiator")
     * @return Reference to channel instance
     */
    private Channel createDefaultChannel(List<String> playerIds, MessageSource messageSource) {
        DefaultChannelImpl channelImpl = new DefaultChannelImpl();

        String primaryId = playerIds.remove(0);
        channelImpl.initPrimaryPlayer(primaryId, messageSource, 10);
        channelImpl.initSecondaryPlayers(playerIds);
        return channelImpl;
    }

    /**
     * Creates IPC channel built on sockets
     * @param channelOpt Channel options string. Must be in "socket:<hostname>:<port>" format
     * @param playerIds List of players Ids to build communication environment
     * @param messageSource Message source that provides messages to send by primary player (aka "initiator")
     * @return Reference to channel instance
     * @throws InitializationException In case of any initialization error
     */
    private Channel createSocketChannel(String channelOpt, List<String> playerIds, MessageSource messageSource)
            throws InitializationException {

        // Expects the following format: "socket:<hostname>:<port>"
        List<String> opts = Arrays.asList(StringUtils.split(channelOpt, ":"));
        if (opts.size() < 3) {
            throw new InitializationException("Socket channel configuration must have the following format: " +
                    "\"socket:<hostname>:<port>\"");
        }

        SocketChannelImpl channelImpl;
        int port = 0;
        try {
            port = Integer.parseInt(opts.get(2));
        }
        catch (NumberFormatException e) {
            throw new InitializationException("Invalid port number \"" + opts.get(2) + "\"");
        }

        channelImpl = new SocketChannelImpl(opts.get(1), port);
        channelImpl.initPlayer(playerIds.get(0), messageSource, 10);

        String toValue = StringUtils.defaultIfBlank(cmdLine.getOptionValue(OPT_TIMEOUT), "0");
        String subValue = StringUtils.defaultIfBlank(cmdLine.getOptionValue(OPT_SUBSCRIBERS), "0");
        long timeout = 0L;
        int subCount = 0;
        try {
            timeout = Long.parseLong(toValue);
        }
        catch (NumberFormatException e) {
            throw new InitializationException("Invalid timeout number \"" + toValue + "\"");
        }

        try {
            subCount = Integer.parseInt(subValue);
        }
        catch (NumberFormatException e) {
            throw new InitializationException("Invalid subscribers count number \"" + subValue + "\"");
        }

        if (timeout <= 0L && subCount < 1) {
            timeout = 180L;
        }
        if (StringUtils.isBlank(toValue) && subCount < 1) {
            subCount = 1;
        }
        if (timeout > 0 && subCount < 1) {
            subCount = Integer.MAX_VALUE;
        }

        channelImpl.initStarter(timeout, subCount);

        return channelImpl;
    }
}
